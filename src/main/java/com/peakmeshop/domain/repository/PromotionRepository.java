package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    List<Promotion> findByCategoryId(Long categoryId);

    Optional<Promotion> findByPromotionCode(String promotionCode);

    Page<Promotion> findByStartDateBeforeAndEndDateAfterAndIsActiveTrue(LocalDateTime now, LocalDateTime now2, Pageable pageable);

    List<Promotion> findByStartDateBeforeAndEndDateAfterAndIsActiveTrue(LocalDateTime now, LocalDateTime now2);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByIsActiveTrue();

    long countByIsActiveTrueAndCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    Page<Promotion> findByIsActive(boolean isActive, Pageable pageable);

    @Query("SELECT p FROM Promotion p WHERE " +
            "LOWER(p.name) LIKE %:keyword% OR " +
            "LOWER(p.description) LIKE %:keyword% OR " +
            "LOWER(p.promotionCode) LIKE %:keyword%")
    Page<Promotion> searchPromotions(@Param("keyword") String keyword, Pageable pageable);
}