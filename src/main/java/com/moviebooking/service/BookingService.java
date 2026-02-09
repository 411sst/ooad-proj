package com.moviebooking.service;

import com.moviebooking.entity.*;
import com.moviebooking.entity.enums.BookingStatus;
import com.moviebooking.exception.BadRequestException;
import com.moviebooking.exception.ResourceNotFoundException;
import com.moviebooking.patterns.chain.BookingValidationChain;
import com.moviebooking.patterns.chain.BookingValidationRequest;
import com.moviebooking.patterns.chain.ValidationResult;
import com.moviebooking.patterns.state.BookingContext;
import com.moviebooking.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);
    private static final BigDecimal GST_RATE = new BigDecimal("0.18");

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final SeatRepository seatRepository;
    private final SeatLockRepository seatLockRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatService seatService;
    private final BookingValidationChain validationChain;

    public BookingService(BookingRepository bookingRepository, BookingSeatRepository bookingSeatRepository,
                         SeatRepository seatRepository, SeatLockRepository seatLockRepository,
                         ShowtimeRepository showtimeRepository,
                         SeatService seatService, BookingValidationChain validationChain) {
        this.bookingRepository = bookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.seatRepository = seatRepository;
        this.seatLockRepository = seatLockRepository;
        this.showtimeRepository = showtimeRepository;
        this.seatService = seatService;
        this.validationChain = validationChain;
    }

    @Transactional
    public Booking createBooking(User user, Long showtimeId, List<Long> seatIds) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime", "id", showtimeId));

        // Chain of Responsibility: validate booking request
        List<Long> bookedSeatIds = bookingSeatRepository.findBookedSeatIdsForShowtime(showtimeId);
        List<Long> lockedSeatIds = seatLockRepository.findLockedSeatIdsForShowtime(showtimeId, LocalDateTime.now());
        long activeBookings = bookingRepository.countActiveBookingsForShowtime(showtimeId);

        BookingValidationRequest validationRequest = BookingValidationRequest.builder()
                .user(user)
                .showtime(showtime)
                .seatIds(seatIds)
                .bookedSeatIds(bookedSeatIds)
                .lockedSeatIds(lockedSeatIds)
                .activeBookingsCount(activeBookings)
                .build();

        ValidationResult validationResult = validationChain.validate(validationRequest);
        if (!validationResult.isValid()) {
            throw new BadRequestException(validationResult.getMessage());
        }

        List<Seat> seats = seatRepository.findByIdIn(seatIds);
        if (seats.size() != seatIds.size()) {
            throw new BadRequestException("One or more seats not found");
        }

        // Calculate ticket amount
        BigDecimal ticketAmount = seats.stream()
                .map(Seat::getBasePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal taxAmount = ticketAmount.multiply(GST_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = ticketAmount.add(taxAmount);

        // Create booking
        Booking booking = new Booking();
        booking.setBookingReference(generateBookingReference());
        booking.setUser(user);
        booking.setShowtime(showtime);
        booking.setMovie(showtime.getMovie());
        booking.setScreen(showtime.getScreen());
        booking.setTheater(showtime.getScreen().getTheater());
        booking.setNumSeats(seatIds.size());
        booking.setTicketAmount(ticketAmount);
        booking.setTaxAmount(taxAmount);
        booking.setTotalAmount(totalAmount);
        booking.setFoodAmount(BigDecimal.ZERO);
        booking.setDiscountAmount(BigDecimal.ZERO);
        booking.setStatus(BookingStatus.PENDING);

        booking = bookingRepository.save(booking);

        // Create booking-seat records
        List<BookingSeat> bookingSeats = new ArrayList<>();
        for (Seat seat : seats) {
            BookingSeat bs = new BookingSeat();
            bs.setBooking(booking);
            bs.setSeat(seat);
            bs.setShowtime(showtime);
            bs.setSeatPrice(seat.getBasePrice());
            bookingSeats.add(bs);
        }
        bookingSeatRepository.saveAll(bookingSeats);

        // State Pattern: transition to LOCKED
        BookingContext context = new BookingContext(booking);
        context.lock();
        booking = bookingRepository.save(booking);

        log.info("Booking {} created for user {} with {} seats, total ₹{}",
                booking.getBookingReference(), user.getEmail(), seatIds.size(), totalAmount);

        return booking;
    }

    @Transactional
    public Booking confirmBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);

        BookingContext context = new BookingContext(booking);
        context.confirm();

        // Update available seats count
        Showtime showtime = booking.getShowtime();
        showtime.setAvailableSeats(showtime.getAvailableSeats() - booking.getNumSeats());
        showtimeRepository.save(showtime);

        // Release seat locks (they're now permanently booked)
        seatService.releaseLocksForBooking(showtime.getId(), booking.getUser().getId());

        booking = bookingRepository.save(booking);
        log.info("Booking {} confirmed", booking.getBookingReference());
        return booking;
    }

    @Transactional
    public Booking cancelBooking(Long bookingId, String reason) {
        Booking booking = getBookingById(bookingId);

        BookingContext context = new BookingContext(booking);
        context.cancel();
        booking.setCancellationReason(reason);

        // Release seats if booking was confirmed
        if (booking.getShowtime() != null) {
            Showtime showtime = booking.getShowtime();
            showtime.setAvailableSeats(showtime.getAvailableSeats() + booking.getNumSeats());
            showtimeRepository.save(showtime);
        }

        // Remove booking seats
        List<BookingSeat> bookingSeats = bookingSeatRepository.findByBookingId(bookingId);
        bookingSeatRepository.deleteAll(bookingSeats);

        booking = bookingRepository.save(booking);
        log.info("Booking {} cancelled: {}", booking.getBookingReference(), reason);
        return booking;
    }

    @Transactional
    public Booking updateFoodAmount(Long bookingId, BigDecimal foodAmount) {
        Booking booking = getBookingById(bookingId);
        booking.setFoodAmount(foodAmount);

        BigDecimal subtotal = booking.getTicketAmount().add(foodAmount);
        BigDecimal tax = subtotal.multiply(GST_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal discount = booking.getDiscountAmount() != null ? booking.getDiscountAmount() : BigDecimal.ZERO;
        booking.setTaxAmount(tax);
        booking.setTotalAmount(subtotal.add(tax).subtract(discount));

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking applyDiscount(Long bookingId, BigDecimal discountAmount, PromoCode promoCode) {
        Booking booking = getBookingById(bookingId);
        booking.setDiscountAmount(discountAmount);
        booking.setPromoCode(promoCode);

        BigDecimal subtotal = booking.getTicketAmount().add(booking.getFoodAmount());
        BigDecimal tax = subtotal.multiply(GST_RATE).setScale(2, RoundingMode.HALF_UP);
        booking.setTaxAmount(tax);
        booking.setTotalAmount(subtotal.add(tax).subtract(discountAmount));

        return bookingRepository.save(booking);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
    }

    public Booking getBookingByReference(String reference) {
        return bookingRepository.findByBookingReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "reference", reference));
    }

    public Page<Booking> getUserBookings(Long userId, Pageable pageable) {
        return bookingRepository.findByUserIdOrderByBookingDatetimeDesc(userId, pageable);
    }

    public List<Booking> getUpcomingBookings(Long userId) {
        return bookingRepository.findUpcomingBookings(userId, LocalDate.now());
    }

    public BigDecimal calculateRefundAmount(Booking booking) {
        LocalDateTime showTime = booking.getShowtime().getShowDatetime();
        long hoursUntilShow = java.time.Duration.between(LocalDateTime.now(), showTime).toHours();

        if (hoursUntilShow > 24) {
            return booking.getTotalAmount(); // 100% refund
        } else if (hoursUntilShow > 6) {
            return booking.getTotalAmount().multiply(new BigDecimal("0.50")).setScale(2, RoundingMode.HALF_UP); // 50%
        }
        return BigDecimal.ZERO; // No refund
    }

    private String generateBookingReference() {
        return "BK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
