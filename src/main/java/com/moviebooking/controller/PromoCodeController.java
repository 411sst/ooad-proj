package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponse;
import com.moviebooking.dto.BookingDto;
import com.moviebooking.entity.Booking;
import com.moviebooking.repository.BookingFoodRepository;
import com.moviebooking.repository.BookingSeatRepository;
import com.moviebooking.service.PromoCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/promo")
public class PromoCodeController {

    private final PromoCodeService promoCodeService;
    private final BookingSeatRepository bookingSeatRepository;
    private final BookingFoodRepository bookingFoodRepository;

    public PromoCodeController(PromoCodeService promoCodeService,
                              BookingSeatRepository bookingSeatRepository,
                              BookingFoodRepository bookingFoodRepository) {
        this.promoCodeService = promoCodeService;
        this.bookingSeatRepository = bookingSeatRepository;
        this.bookingFoodRepository = bookingFoodRepository;
    }

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<BookingDto>> applyPromoCode(@RequestBody Map<String, Object> request) {
        Long bookingId = Long.valueOf(request.get("bookingId").toString());
        String code = (String) request.get("code");

        Booking booking = promoCodeService.applyPromoCode(bookingId, code);
        BookingDto dto = BookingDto.fromEntity(booking,
                bookingSeatRepository.findByBookingId(bookingId),
                bookingFoodRepository.findByBookingId(bookingId));
        return ResponseEntity.ok(ApiResponse.success("Promo code applied!", dto));
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validatePromoCode(@RequestBody Map<String, Object> request) {
        String code = (String) request.get("code");
        BigDecimal subtotal = new BigDecimal(request.get("subtotal").toString());

        BigDecimal discount = promoCodeService.validatePromoCode(code, subtotal);
        Map<String, Object> result = Map.of(
                "code", code,
                "discount", discount,
                "newTotal", subtotal.subtract(discount)
        );
        return ResponseEntity.ok(ApiResponse.success("Promo code valid", result));
    }
}
