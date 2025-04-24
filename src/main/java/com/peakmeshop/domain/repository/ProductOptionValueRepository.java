package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.ProductOptionValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ProductOptionValueRepository extends JpaRepository<ProductOptionValue, Long> {
    Page<ProductOptionValue> findByOptionId(Long optionId, Pageable pageable);
    Page<ProductOptionValue> findByOptionProductId(Long productId, Pageable pageable);
} 