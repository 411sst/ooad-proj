package com.moviebooking.service;

import com.moviebooking.entity.Booking;
import com.moviebooking.entity.PromoCode;
import com.moviebooking.entity.enums.DiscountType;
import com.moviebooking.exception.BadRequestException;
import com.moviebooking.exception.ResourceNotFoundException;
import com.moviebooking.repository.PromoCodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class PromoCodeService {

    private static final Logger log = LoggerFactory.getLogger(PromoCodeService.class);

    private final PromoCodeRepository promoCodeRepository;
    private final BookingService bookingService;

    public PromoCodeService(PromoCodeRepository promoCodeRepository, BookingService bookingService) {
        this.promoCodeRepository = promoCodeRepository;
        this.bookingService = bookingService;
    }

    /**
     * Validate and apply a promo code to a booking.
     */
    @Transactional
    public Booking applyPromoCode(Long bookingId, String code) {
        PromoCode promo = promoCodeRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("PromoCode", "code", code));

        // Validate promo code
        LocalDateTime now = LocalDateTime.now();
        if (!promo.getIsActive()) {
            throw new BadRequestException("This promo code is no longer active");
        }
        if (now.isBefore(promo.getValidFrom()) || now.isAfter(promo.getValidUntil())) {
            throw new BadRequestException("This promo code has expired");
        }
        if (promo.getCurrentUsage() >= promo.getMaxUsage()) {
            throw new BadRequestException("This promo code has reached its usage limit");
        }

        Booking booking = bookingService.getBookingById(bookingId);

        // Check minimum amount
        BigDecimal subtotal = booking.getTicketAmount().add(booking.getFoodAmount());
        if (promo.getMinimumAmount() != null && subtotal.compareTo(promo.getMinimumAmount()) < 0) {
            throw new BadRequestException("Minimum order amount of ₹" + promo.getMinimumAmount() + " required for this promo code");
        }

        // Calculate discount
        BigDecimal discount;
        if (promo.getDiscountType() == DiscountType.PERCENTAGE) {
            discount = subtotal.multiply(promo.getDiscountValue().divide(BigDecimal.valueOf(100)))
                    .setScale(2, RoundingMode.HALF_UP);
            // Cap at max discount if specified
            if (promo.getMaxDiscount() != null && discount.compareTo(promo.getMaxDiscount()) > 0) {
                discount = promo.getMaxDiscount();
            }
        } else {
            discount = promo.getDiscountValue();
        }

        // Apply discount to booking
        Booking updated = bookingService.applyDiscount(bookingId, discount, promo);

        // Increment usage count
        promo.setCurrentUsage(promo.getCurrentUsage() + 1);
        promoCodeRepository.save(promo);

        log.info("Promo code {} applied to booking {}: discount ₹{}", code, booking.getBookingReference(), discount);
        return updated;
    }

    /**
     * Validate a promo code without applying it.
     */
    public BigDecimal validatePromoCode(String code, BigDecimal subtotal) {
        PromoCode promo = promoCodeRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("PromoCode", "code", code));

        LocalDateTime now = LocalDateTime.now();
        if (!promo.getIsActive() || now.isBefore(promo.getValidFrom()) || now.isAfter(promo.getValidUntil())) {
            throw new BadRequestException("Invalid or expired promo code");
        }
        if (promo.getCurrentUsage() >= promo.getMaxUsage()) {
            throw new BadRequestException("Promo code usage limit reached");
        }
        if (promo.getMinimumAmount() != null && subtotal.compareTo(promo.getMinimumAmount()) < 0) {
            throw new BadRequestException("Minimum amount ₹" + promo.getMinimumAmount() + " required");
        }

        BigDecimal discount;
        if (promo.getDiscountType() == DiscountType.PERCENTAGE) {
            discount = subtotal.multiply(promo.getDiscountValue().divide(BigDecimal.valueOf(100)))
                    .setScale(2, RoundingMode.HALF_UP);
            if (promo.getMaxDiscount() != null && discount.compareTo(promo.getMaxDiscount()) > 0) {
                discount = promo.getMaxDiscount();
            }
        } else {
            discount = promo.getDiscountValue();
        }

        return discount;
    }
}
