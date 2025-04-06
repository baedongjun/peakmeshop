package com.peakmeshop.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.peakmeshop.domain.entity.ProductQna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductQnaRepository extends JpaRepository<ProductQna, Long> {
    Long countByProductId(Long productId);
    Page<ProductQna> findByProductId(Long productId, Pageable pageable);
} 