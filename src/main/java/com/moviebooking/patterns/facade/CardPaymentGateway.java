package com.moviebooking.patterns.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

/**
 * Facade Pattern - Concrete gateway: Card payment with Luhn validation and mock scenarios.
 * Cards ending in 0000: Always success
 * Cards ending in 1111: Always failure (insufficient funds)
 * Cards ending in 2222: Random timeout (50%)
 * Other cards: 90% success, 10% random failure
 * Owned by: Vaishnav
 */
@Component
public class CardPaymentGateway implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(CardPaymentGateway.class);
    private final Random random = new Random();

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        log.info("Processing card payment for booking {} amount ₹{}", request.getBookingId(), request.getAmount());

        String cardNumber = request.getCardNumber() != null ? request.getCardNumber().replaceAll("\\s", "") : "";

        // Validate card number using Luhn algorithm
        if (!isValidLuhn(cardNumber) && cardNumber.length() < 13) {
            return PaymentResult.failure(getGatewayName(), "Invalid card number");
        }

        // Validate expiry
        if (request.getCardExpiry() == null || request.getCardExpiry().isEmpty()) {
            return PaymentResult.failure(getGatewayName(), "Card expiry is required");
        }

        // Simulate processing delay
        simulateDelay(2000, 3000);

        // Mock scenarios based on card ending
        String lastFour = cardNumber.length() >= 4 ? cardNumber.substring(cardNumber.length() - 4) : "0000";

        return switch (lastFour) {
            case "1111" -> PaymentResult.failure(getGatewayName(), "Insufficient funds");
            case "2222" -> {
                if (random.nextBoolean()) {
                    yield PaymentResult.failure(getGatewayName(), "Transaction timeout");
                }
                yield PaymentResult.success(generateTransactionId(), getGatewayName(), request.getAmount());
            }
            case "3333" -> PaymentResult.pending(generateTransactionId(), getGatewayName(), "3D Secure verification required");
            default -> {
                if (random.nextInt(100) < 90) { // 90% success
                    yield PaymentResult.success(generateTransactionId(), getGatewayName(), request.getAmount());
                }
                yield PaymentResult.failure(getGatewayName(), "Transaction declined by bank");
            }
        };
    }

    @Override
    public PaymentResult checkStatus(String transactionId) {
        return PaymentResult.success(transactionId, getGatewayName(), null);
    }

    @Override
    public PaymentResult processRefund(String transactionId, BigDecimal amount) {
        log.info("Processing card refund for transaction {} amount ₹{}", transactionId, amount);
        simulateDelay(1000, 2000);
        String refundTxnId = "REF-" + generateTransactionId();
        return PaymentResult.success(refundTxnId, getGatewayName(), amount);
    }

    @Override
    public String getGatewayName() {
        return "CardPaymentGateway";
    }

    private boolean isValidLuhn(String number) {
        if (number == null || number.isEmpty()) return false;
        int sum = 0;
        boolean alternate = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(number.charAt(i));
            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }

    private String generateTransactionId() {
        return "CARD-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }

    private void simulateDelay(int minMs, int maxMs) {
        try {
            Thread.sleep(minMs + random.nextInt(maxMs - minMs));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
