package com.moviebooking.repository;

import com.moviebooking.entity.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {

    Optional<PromoCode> findByCode(String code);

    @Query("SELECT pc FROM PromoCode pc WHERE pc.isActive = true AND pc.validFrom <= :now AND pc.validUntil >= :now AND pc.currentUsage < pc.maxUsage")
    List<PromoCode> findActivePromoCodes(@Param("now") LocalDateTime now);

    boolean existsByCode(String code);
}
