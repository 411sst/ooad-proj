package com.moviebooking.controller;

import com.moviebooking.dto.*;
import com.moviebooking.entity.Booking;
import com.moviebooking.entity.BookingFood;
import com.moviebooking.entity.BookingSeat;
import com.moviebooking.entity.User;
import com.moviebooking.repository.BookingFoodRepository;
import com.moviebooking.repository.BookingSeatRepository;
import com.moviebooking.service.BookingService;
import com.moviebooking.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final BookingSeatRepository bookingSeatRepository;
    private final BookingFoodRepository bookingFoodRepository;

    public BookingController(BookingService bookingService, UserService userService,
                            BookingSeatRepository bookingSeatRepository,
                            BookingFoodRepository bookingFoodRepository) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.bookingSeatRepository = bookingSeatRepository;
        this.bookingFoodRepository = bookingFoodRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<BookingDto>> createBooking(
            @Valid @RequestBody CreateBookingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        Booking booking = bookingService.createBooking(user, request.getShowtimeId(), request.getSeatIds());
        BookingDto dto = buildBookingDto(booking);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Booking created, seats locked for 10 minutes", dto));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingDto>> getBooking(@PathVariable Long bookingId) {
        Booking booking = bookingService.getBookingById(bookingId);
        BookingDto dto = buildBookingDto(booking);
        return ResponseEntity.ok(ApiResponse.success("Booking retrieved", dto));
    }

    @GetMapping("/reference/{ref}")
    public ResponseEntity<ApiResponse<BookingDto>> getBookingByReference(@PathVariable String ref) {
        Booking booking = bookingService.getBookingByReference(ref);
        BookingDto dto = buildBookingDto(booking);
        return ResponseEntity.ok(ApiResponse.success("Booking retrieved", dto));
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<ApiResponse<Page<BookingDto>>> getMyBookings(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        Page<Booking> bookings = bookingService.getUserBookings(user.getId(), PageRequest.of(page, size));
        Page<BookingDto> dtoPage = bookings.map(this::buildBookingDto);
        return ResponseEntity.ok(ApiResponse.success("Bookings retrieved", dtoPage));
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<BookingDto>> cancelBooking(
            @PathVariable Long bookingId,
            @RequestParam(defaultValue = "User requested cancellation") String reason) {
        Booking booking = bookingService.cancelBooking(bookingId, reason);
        BookingDto dto = buildBookingDto(booking);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled", dto));
    }

    private BookingDto buildBookingDto(Booking booking) {
        List<BookingSeat> seats = bookingSeatRepository.findByBookingId(booking.getId());
        List<BookingFood> foods = bookingFoodRepository.findByBookingId(booking.getId());
        return BookingDto.fromEntity(booking, seats, foods);
    }
}
