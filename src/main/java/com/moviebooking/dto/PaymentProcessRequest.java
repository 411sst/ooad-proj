package com.moviebooking.dto;

import com.moviebooking.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentProcessRequest {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    // Card fields
    private String cardNumber;
    private String cardExpiry;
    private String cardCvv;
    private String cardHolderName;

    // UPI fields
    private String upiId;

    // Net Banking fields
    private String bankName;

    // Wallet fields
    private String walletName;
}
