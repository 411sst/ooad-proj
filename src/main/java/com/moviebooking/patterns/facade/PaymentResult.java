package com.moviebooking.patterns.facade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PaymentResult {
    private boolean success;
    private String transactionId;
    private String gatewayName;
    private String message;
    private BigDecimal amount;
    private String status; // SUCCESS, FAILED, PENDING
    private LocalDateTime timestamp;
    private String failureReason;

    public static PaymentResult success(String transactionId, String gateway, BigDecimal amount) {
        return PaymentResult.builder()
                .success(true)
                .transactionId(transactionId)
                .gatewayName(gateway)
                .message("Payment processed successfully")
                .amount(amount)
                .status("SUCCESS")
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static PaymentResult failure(String gateway, String reason) {
        return PaymentResult.builder()
                .success(false)
                .gatewayName(gateway)
                .message("Payment failed: " + reason)
                .status("FAILED")
                .failureReason(reason)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static PaymentResult pending(String transactionId, String gateway, String message) {
        return PaymentResult.builder()
                .success(false)
                .transactionId(transactionId)
                .gatewayName(gateway)
                .message(message)
                .status("PENDING")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
