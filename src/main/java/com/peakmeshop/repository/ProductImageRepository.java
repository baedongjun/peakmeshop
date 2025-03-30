package com.peakmeshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peakmeshop.entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    /**
     * 상품 ID로 이미지 목록 조회
     */
    List<ProductImage> findByProductId(Long productId);

    /**
     * 상품 ID로 이미지 목록 조회 (정렬 순서대로)
     */
    List<ProductImage> findByProductIdOrderBySortOrderAsc(Long productId);

    /**
     * 상품 ID로 대표 이미지 조회
     */
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId AND pi.isMain = true")
    ProductImage findMainImageByProductId(@Param("productId") Long productId);

    /**
     * 상품 ID로 이미지 삭제
     */
    void deleteByProductId(Long productId);

}