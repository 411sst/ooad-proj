package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponse;
import com.moviebooking.dto.PaymentProcessRequest;
import com.moviebooking.entity.Payment;
import com.moviebooking.patterns.facade.PaymentRequest;
import com.moviebooking.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    public ResponseEntity<ApiResponse<Map<String, Object>>> processPayment(
            @Valid @RequestBody PaymentProcessRequest request) {

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .bookingId(request.getBookingId())
                .paymentMethod(request.getPaymentMethod())
                .cardNumber(request.getCardNumber())
                .cardExpiry(request.getCardExpiry())
                .cardCvv(request.getCardCvv())
                .cardHolderName(request.getCardHolderName())
                .upiId(request.getUpiId())
                .bankName(request.getBankName())
                .walletName(request.getWalletName())
                .build();

        Payment payment = paymentService.processPayment(
                request.getBookingId(), request.getPaymentMethod(), paymentRequest);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("paymentId", payment.getId());
        result.put("transactionId", payment.getTransactionId());
        result.put("status", payment.getStatus().name());
        result.put("amount", payment.getAmount());
        result.put("gateway", payment.getGatewayName());

        if (payment.getFailureReason() != null) {
            result.put("failureReason", payment.getFailureReason());
        }

        boolean isSuccess = "SUCCESS".equals(payment.getStatus().name());
        String message = isSuccess ? "Payment successful! Booking confirmed." : "Payment failed: " + payment.getFailureReason();

        return ResponseEntity.ok(new ApiResponse<>(isSuccess, message, result));
    }

    @PostMapping("/{bookingId}/refund")
    public ResponseEntity<ApiResponse<Map<String, Object>>> processRefund(@PathVariable Long bookingId) {
        Payment payment = paymentService.processRefund(bookingId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("refundAmount", payment.getRefundAmount());
        result.put("refundTransactionId", payment.getRefundTransactionId());
        result.put("status", payment.getStatus().name());

        return ResponseEntity.ok(ApiResponse.success("Refund processed successfully", result));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPaymentDetails(@PathVariable Long bookingId) {
        Payment payment = paymentService.getPaymentByBookingId(bookingId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("paymentId", payment.getId());
        result.put("transactionId", payment.getTransactionId());
        result.put("method", payment.getPaymentMethod().name());
        result.put("status", payment.getStatus().name());
        result.put("amount", payment.getAmount());
        result.put("gateway", payment.getGatewayName());
        result.put("paymentDatetime", payment.getPaymentDatetime());

        return ResponseEntity.ok(ApiResponse.success("Payment details retrieved", result));
    }
}
