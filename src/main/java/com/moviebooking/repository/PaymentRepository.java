package com.moviebooking.repository;

import com.moviebooking.entity.Payment;
import com.moviebooking.entity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBookingId(Long bookingId);

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByStatus(PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.status = 'SUCCESS' AND p.paymentDatetime BETWEEN :start AND :end")
    List<Payment> findSuccessfulPaymentsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
