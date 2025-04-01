package com.peakmeshop.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.domain.entity.ProductComparison;

public interface ProductComparisonRepository extends JpaRepository<ProductComparison, Long> {

    Optional<ProductComparison> findByMemberIdAndIsRecentFalse(Long memberId);

    Optional<ProductComparison> findByMemberIdAndIsRecentTrue(Long memberId);
}