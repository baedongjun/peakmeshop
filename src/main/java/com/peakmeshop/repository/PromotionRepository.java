package com.peakmeshop.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.entity.Promotion;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    List<Promotion> findByCategoryId(Long categoryId);

    Optional<Promotion> findByPromotionCode(String promotionCode);

    Page<Promotion> findByStartDateBeforeAndEndDateAfterAndActiveTrue(LocalDateTime now, LocalDateTime now2, Pageable pageable);

    List<Promotion> findByStartDateBeforeAndEndDateAfterAndActiveTrue(LocalDateTime now, LocalDateTime now2);
}