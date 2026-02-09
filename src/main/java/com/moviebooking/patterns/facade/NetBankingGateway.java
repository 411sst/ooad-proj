package com.moviebooking.patterns.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

/**
 * Facade Pattern - Concrete gateway: Net Banking payment simulation.
 * Simulates redirect-based bank page interaction.
 * Owned by: Vaishnav
 */
@Component
public class NetBankingGateway implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(NetBankingGateway.class);
    private final Random random = new Random();

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        log.info("Processing Net Banking payment for booking {} via {} amount ₹{}",
                request.getBookingId(), request.getBankName(), request.getAmount());

        if (request.getBankName() == null || request.getBankName().isEmpty()) {
            return PaymentResult.failure(getGatewayName(), "Bank name is required");
        }

        // Simulate bank page processing
        simulateDelay(3000, 5000);

        // 85% success rate
        if (random.nextInt(100) < 85) {
            return PaymentResult.success(generateTransactionId(), getGatewayName(), request.getAmount());
        }

        return PaymentResult.failure(getGatewayName(), "Net banking transaction failed - session expired");
    }

    @Override
    public PaymentResult checkStatus(String transactionId) {
        return PaymentResult.success(transactionId, getGatewayName(), null);
    }

    @Override
    public PaymentResult processRefund(String transactionId, BigDecimal amount) {
        log.info("Processing Net Banking refund for transaction {} amount ₹{}", transactionId, amount);
        simulateDelay(2000, 3000);
        return PaymentResult.success("REF-" + generateTransactionId(), getGatewayName(), amount);
    }

    @Override
    public String getGatewayName() {
        return "NetBankingGateway";
    }

    private String generateTransactionId() {
        return "NB-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }

    private void simulateDelay(int minMs, int maxMs) {
        try {
            Thread.sleep(minMs + random.nextInt(maxMs - minMs));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
