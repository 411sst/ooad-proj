package com.moviebooking.service;

import com.moviebooking.entity.Booking;
import com.moviebooking.entity.Payment;
import com.moviebooking.entity.enums.BookingStatus;
import com.moviebooking.entity.enums.PaymentMethod;
import com.moviebooking.entity.enums.PaymentStatus;
import com.moviebooking.exception.BadRequestException;
import com.moviebooking.exception.ResourceNotFoundException;
import com.moviebooking.patterns.facade.PaymentFacade;
import com.moviebooking.patterns.facade.PaymentRequest;
import com.moviebooking.patterns.facade.PaymentResult;
import com.moviebooking.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final PaymentFacade paymentFacade;
    private final BookingService bookingService;
    private final QRCodeService qrCodeService;
    private final EmailService emailService;

    public PaymentService(PaymentRepository paymentRepository, PaymentFacade paymentFacade,
                         BookingService bookingService, QRCodeService qrCodeService,
                         EmailService emailService) {
        this.paymentRepository = paymentRepository;
        this.paymentFacade = paymentFacade;
        this.bookingService = bookingService;
        this.qrCodeService = qrCodeService;
        this.emailService = emailService;
    }

    @Transactional
    public Payment processPayment(Long bookingId, PaymentMethod method, PaymentRequest paymentRequest) {
        Booking booking = bookingService.getBookingById(bookingId);

        if (booking.getStatus() != BookingStatus.LOCKED) {
            throw new BadRequestException("Booking must be in LOCKED state to process payment. Current: " + booking.getStatus());
        }

        // Create payment record
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setUser(booking.getUser());
        payment.setAmount(booking.getTotalAmount());
        payment.setPaymentMethod(method);
        payment.setStatus(PaymentStatus.PROCESSING);
        payment = paymentRepository.save(payment);

        // Route to appropriate gateway via Facade
        paymentRequest.setBookingId(bookingId);
        paymentRequest.setAmount(booking.getTotalAmount());
        paymentRequest.setPaymentMethod(method);
        paymentRequest.setUserId(booking.getUser().getId());

        PaymentResult result = paymentFacade.processPayment(paymentRequest);

        if (result.isSuccess()) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(result.getTransactionId());
            payment.setGatewayName(result.getGatewayName());
            payment.setSuccessDatetime(LocalDateTime.now());

            // Confirm booking using State Pattern
            bookingService.confirmBooking(bookingId);

            // Generate QR code
            String qrCodePath = qrCodeService.generateQRCode(booking);
            booking.setQrCodeUrl(qrCodePath);

            // Send confirmation email
            emailService.sendBookingConfirmation(booking);

            log.info("Payment successful for booking {} via {}, txn: {}",
                    booking.getBookingReference(), result.getGatewayName(), result.getTransactionId());
        } else {
            payment.setStatus("PENDING".equals(result.getStatus()) ? PaymentStatus.PROCESSING : PaymentStatus.FAILED);
            payment.setFailureReason(result.getFailureReason());
            payment.setGatewayName(result.getGatewayName());

            log.warn("Payment failed for booking {}: {}", booking.getBookingReference(), result.getFailureReason());
        }

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment processRefund(Long bookingId) {
        Booking booking = bookingService.getBookingById(bookingId);
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "bookingId", bookingId));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new BadRequestException("Can only refund successful payments");
        }

        BigDecimal refundAmount = bookingService.calculateRefundAmount(booking);
        if (refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("No refund applicable for this booking (less than 6 hours before show)");
        }

        PaymentResult refundResult = paymentFacade.processRefund(
                payment.getPaymentMethod(), payment.getTransactionId(), refundAmount);

        if (refundResult.isSuccess()) {
            payment.setRefundAmount(refundAmount);
            payment.setRefundTransactionId(refundResult.getTransactionId());
            payment.setRefundDatetime(LocalDateTime.now());
            payment.setStatus(PaymentStatus.REFUNDED);

            // Cancel booking
            bookingService.cancelBooking(bookingId, "User requested cancellation");

            // Send cancellation email
            emailService.sendBookingCancellation(booking, refundAmount);

            log.info("Refund of ₹{} processed for booking {}", refundAmount, booking.getBookingReference());
        }

        return paymentRepository.save(payment);
    }

    public Payment getPaymentByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "bookingId", bookingId));
    }
}
