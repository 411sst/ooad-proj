package com.moviebooking.patterns.facade;

import com.moviebooking.entity.enums.PaymentMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Facade Pattern - Unified interface for all payment gateways.
 * Routes payment requests to the appropriate concrete gateway based on payment method.
 * Client code interacts only with this facade, never with individual gateways.
 * Owned by: Vaishnav
 */
@Component
public class PaymentFacade {

    private static final Logger log = LoggerFactory.getLogger(PaymentFacade.class);

    private final Map<PaymentMethod, PaymentGateway> gatewayMap = new HashMap<>();

    public PaymentFacade(CardPaymentGateway cardGateway,
                         UPIPaymentGateway upiGateway,
                         NetBankingGateway netBankingGateway,
                         WalletGateway walletGateway) {
        gatewayMap.put(PaymentMethod.CREDIT_CARD, cardGateway);
        gatewayMap.put(PaymentMethod.DEBIT_CARD, cardGateway);
        gatewayMap.put(PaymentMethod.UPI, upiGateway);
        gatewayMap.put(PaymentMethod.NET_BANKING, netBankingGateway);
        gatewayMap.put(PaymentMethod.WALLET, walletGateway);
        log.info("PaymentFacade initialized with {} gateways", gatewayMap.size());
    }

    public PaymentResult processPayment(PaymentRequest request) {
        PaymentGateway gateway = getGateway(request.getPaymentMethod());
        log.info("Routing payment to {} for booking {} (method: {})",
                gateway.getGatewayName(), request.getBookingId(), request.getPaymentMethod());
        return gateway.processPayment(request);
    }

    public PaymentResult checkPaymentStatus(PaymentMethod method, String transactionId) {
        PaymentGateway gateway = getGateway(method);
        return gateway.checkStatus(transactionId);
    }

    public PaymentResult processRefund(PaymentMethod method, String transactionId, BigDecimal amount) {
        PaymentGateway gateway = getGateway(method);
        log.info("Routing refund to {} for transaction {} amount ₹{}", gateway.getGatewayName(), transactionId, amount);
        return gateway.processRefund(transactionId, amount);
    }

    private PaymentGateway getGateway(PaymentMethod method) {
        PaymentGateway gateway = gatewayMap.get(method);
        if (gateway == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + method);
        }
        return gateway;
    }
}
