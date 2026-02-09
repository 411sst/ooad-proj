package com.moviebooking.patterns.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Facade Pattern - Concrete gateway: Wallet payment with mock balance.
 * Owned by: Vaishnav
 */
@Component
public class WalletGateway implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(WalletGateway.class);

    // Mock wallet balances (in real app this would query wallet provider)
    private final Map<String, BigDecimal> walletBalances = new HashMap<>() {{
        put("default", new BigDecimal("5000.00"));
    }};

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        log.info("Processing Wallet payment for booking {} amount ₹{}", request.getBookingId(), request.getAmount());

        BigDecimal balance = walletBalances.getOrDefault("default", new BigDecimal("5000.00"));

        // Simulate quick wallet deduction
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        if (balance.compareTo(request.getAmount()) >= 0) {
            walletBalances.put("default", balance.subtract(request.getAmount()));
            return PaymentResult.success(generateTransactionId(), getGatewayName(), request.getAmount());
        }

        return PaymentResult.failure(getGatewayName(), "Insufficient wallet balance (Available: ₹" + balance + ")");
    }

    @Override
    public PaymentResult checkStatus(String transactionId) {
        return PaymentResult.success(transactionId, getGatewayName(), null);
    }

    @Override
    public PaymentResult processRefund(String transactionId, BigDecimal amount) {
        log.info("Processing Wallet refund for transaction {} amount ₹{}", transactionId, amount);
        BigDecimal balance = walletBalances.getOrDefault("default", BigDecimal.ZERO);
        walletBalances.put("default", balance.add(amount));
        return PaymentResult.success("REF-" + generateTransactionId(), getGatewayName(), amount);
    }

    @Override
    public String getGatewayName() {
        return "WalletGateway";
    }

    private String generateTransactionId() {
        return "WAL-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }
}
