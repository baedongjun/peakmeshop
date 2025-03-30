package com.peakmeshop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.entity.ProductBundle;

public interface ProductBundleRepository extends JpaRepository<ProductBundle, Long> {

    // 활성화된 상품 묶음 조회 (페이징)
    Page<ProductBundle> findByActiveTrue(Pageable pageable);

    // 활성화된 상품 묶음 조회 (리스트)
    List<ProductBundle> findByActiveTrue();
}