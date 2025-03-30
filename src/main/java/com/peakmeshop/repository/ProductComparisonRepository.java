package com.peakmeshop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.entity.ProductComparison;

public interface ProductComparisonRepository extends JpaRepository<ProductComparison, Long> {

    Optional<ProductComparison> findByMemberIdAndIsRecentFalse(Long memberId);

    Optional<ProductComparison> findByMemberIdAndIsRecentTrue(Long memberId);
}