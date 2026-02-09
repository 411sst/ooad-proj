package com.moviebooking.patterns.facade;

import java.math.BigDecimal;

/**
 * Facade Pattern - Common interface for all payment gateways.
 * Owned by: Vaishnav
 */
public interface PaymentGateway {

    PaymentResult processPayment(PaymentRequest request);

    PaymentResult checkStatus(String transactionId);

    PaymentResult processRefund(String transactionId, BigDecimal amount);

    String getGatewayName();
}
