package com.peakmeshop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.entity.ProductComparisonItem;

public interface ProductComparisonItemRepository extends JpaRepository<ProductComparisonItem, Long> {

    List<ProductComparisonItem> findByComparisonId(Long comparisonId);

    Optional<ProductComparisonItem> findByComparisonIdAndProductId(Long comparisonId, Long productId);

    void deleteByComparisonId(Long comparisonId);

    void deleteByComparisonIdAndProductId(Long comparisonId, Long productId);
}