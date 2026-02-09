package com.moviebooking.patterns.facade;

import com.moviebooking.entity.enums.PaymentMethod;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class PaymentRequest {
    private Long bookingId;
    private Long userId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String cardNumber;
    private String cardExpiry;
    private String cardCvv;
    private String cardHolderName;
    private String upiId;
    private String bankName;
    private String walletName;
}
