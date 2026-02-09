package com.moviebooking.patterns.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

/**
 * Facade Pattern - Concrete gateway: UPI payment simulation.
 * Simulates async UPI flow with auto-success after brief delay.
 * Owned by: Vaishnav
 */
@Component
public class UPIPaymentGateway implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(UPIPaymentGateway.class);
    private final Random random = new Random();

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        log.info("Processing UPI payment for booking {} amount ₹{}", request.getBookingId(), request.getAmount());

        if (request.getUpiId() == null || !request.getUpiId().contains("@")) {
            return PaymentResult.failure(getGatewayName(), "Invalid UPI ID format");
        }

        // Simulate UPI processing
        simulateDelay(1000, 2000);

        // 95% success rate for UPI
        if (random.nextInt(100) < 95) {
            return PaymentResult.success(generateTransactionId(), getGatewayName(), request.getAmount());
        }

        return PaymentResult.failure(getGatewayName(), "UPI transaction failed - bank server unavailable");
    }

    @Override
    public PaymentResult checkStatus(String transactionId) {
        return PaymentResult.success(transactionId, getGatewayName(), null);
    }

    @Override
    public PaymentResult processRefund(String transactionId, BigDecimal amount) {
        log.info("Processing UPI refund for transaction {} amount ₹{}", transactionId, amount);
        simulateDelay(1000, 2000);
        return PaymentResult.success("REF-" + generateTransactionId(), getGatewayName(), amount);
    }

    @Override
    public String getGatewayName() {
        return "UPIPaymentGateway";
    }

    private String generateTransactionId() {
        return "UPI-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }

    private void simulateDelay(int minMs, int maxMs) {
        try {
            Thread.sleep(minMs + random.nextInt(maxMs - minMs));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
