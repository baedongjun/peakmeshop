package com.peakmeshop.domain.repository;

import java.util.List;

import com.peakmeshop.domain.entity.ProductBundle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductBundleRepository extends JpaRepository<ProductBundle, Long> {

    // 활성화된 상품 묶음 조회 (페이징)
    Page<ProductBundle> findByIsActiveTrue(Pageable pageable);

    // 활성화된 상품 묶음 조회 (리스트)
    List<ProductBundle> findByIsActiveTrue();
}