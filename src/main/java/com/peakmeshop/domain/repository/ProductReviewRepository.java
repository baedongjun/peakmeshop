package com.peakmeshop.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.peakmeshop.domain.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    Long countByProductId(Long productId);
    Page<ProductReview> findByProductId(Long productId, Pageable pageable);
} 