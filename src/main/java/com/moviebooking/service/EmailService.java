package com.moviebooking.service;

import com.moviebooking.entity.Booking;
import com.moviebooking.entity.BookingSeat;
import com.moviebooking.repository.BookingSeatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Email Service - Console-based implementation for development.
 * Logs formatted email content. Can be switched to real SMTP by injecting JavaMailSender.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final BookingSeatRepository bookingSeatRepository;

    public EmailService(BookingSeatRepository bookingSeatRepository) {
        this.bookingSeatRepository = bookingSeatRepository;
    }

    public void sendBookingConfirmation(Booking booking) {
        List<BookingSeat> seats = bookingSeatRepository.findByBookingId(booking.getId());
        String seatLabels = seats.stream()
                .map(bs -> bs.getSeat().getSeatLabel())
                .collect(Collectors.joining(", "));

        String email = String.format("""
            ============================================
            BOOKING CONFIRMATION EMAIL
            ============================================
            To: %s
            Subject: Booking Confirmed - %s

            Dear %s,

            Your booking has been confirmed!

            Booking Reference: %s
            Movie: %s
            Theater: %s
            Screen: %s
            Date & Time: %s
            Seats: %s

            Ticket Amount: Rs.%s
            Food Amount: Rs.%s
            Tax (GST 18%%): Rs.%s
            Discount: Rs.%s
            ----------------------------
            TOTAL PAID: Rs.%s

            Please show your QR code at the theater entrance.

            Enjoy the show!
            Team MovieBook
            ============================================
            """,
                booking.getUser().getEmail(),
                booking.getMovie().getTitle(),
                booking.getUser().getFullName(),
                booking.getBookingReference(),
                booking.getMovie().getTitle(),
                booking.getTheater().getName(),
                booking.getScreen().getScreenName(),
                booking.getShowtime().getShowDatetime(),
                seatLabels,
                booking.getTicketAmount(),
                booking.getFoodAmount(),
                booking.getTaxAmount(),
                booking.getDiscountAmount(),
                booking.getTotalAmount()
        );

        log.info("\n{}", email);
    }

    public void sendBookingCancellation(Booking booking, BigDecimal refundAmount) {
        String email = String.format("""
            ============================================
            BOOKING CANCELLATION EMAIL
            ============================================
            To: %s
            Subject: Booking Cancelled - %s

            Dear %s,

            Your booking has been cancelled.

            Booking Reference: %s
            Movie: %s

            Original Amount: Rs.%s
            Refund Amount: Rs.%s

            Refund will be credited to your original payment method within 3-7 business days.

            Team MovieBook
            ============================================
            """,
                booking.getUser().getEmail(),
                booking.getBookingReference(),
                booking.getUser().getFullName(),
                booking.getBookingReference(),
                booking.getMovie().getTitle(),
                booking.getTotalAmount(),
                refundAmount
        );

        log.info("\n{}", email);
    }
}
