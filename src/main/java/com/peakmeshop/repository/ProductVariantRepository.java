package com.peakmeshop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.peakmeshop.entity.ProductVariant;

import java.util.Map;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    Page<ProductVariant> findByProductId(Long productId, Pageable pageable);

    List<ProductVariant> findByProductId(Long productId);

    Optional<ProductVariant> findBySku(String sku);

}