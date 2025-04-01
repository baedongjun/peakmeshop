package com.peakmeshop.domain.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peakmeshop.domain.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByCode(String code);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findByBrand(String brand, Pageable pageable);

    Page<Product> findByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable);

    Page<Product> findByNameContainingOrDescriptionContainingAndCategoryId(String name, String description, Long categoryId, Pageable pageable);

    Page<Product> findByFeaturedTrueAndActiveTrue(Pageable pageable);

    Page<Product> findByActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<Product> findByActiveTrueOrderBySalesCountDesc(Pageable pageable);

    Page<Product> findByActiveTrueAndSalePriceIsNotNullOrderBySalePriceAsc(Pageable pageable);

    List<Product> findTop10ByActiveTrueOrderBySalesCountDesc();

    long countByCategoryId(Long categoryId);

    // 추가: 카테고리 ID로 상품 조회 (특정 상품 제외)
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.id != :productId")
    List<Product> findByCategoryIdAndIdNot(@Param("categoryId") Long categoryId, @Param("productId") Long productId, Pageable pageable);

    // 추가: 판매량 기준 정렬
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId ORDER BY p.salesCount DESC")
    List<Product> findByCategoryIdOrderBySalesCountDesc(@Param("categoryId") Long categoryId, Pageable pageable);

    // 추가: 전체 상품 판매량 기준 정렬
    @Query("SELECT p FROM Product p ORDER BY p.salesCount DESC")
    List<Product> findAllByOrderBySalesCountDesc(Pageable pageable);

    Page<Product> findByBrandId(Long brandId, Pageable pageable);

    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Product> findByStockGreaterThan(Integer stock, Pageable pageable);

    Page<Product> findByStockEquals(Integer stock, Pageable pageable);

    Page<Product> findBySalePriceIsNotNull(Pageable pageable);

    Page<Product> findBySalePriceIsNull(Pageable pageable);

    long countByStockGreaterThan(Integer stock);

    long countByStockEquals(Integer stock);

    @Query("SELECT MIN(p.price) FROM Product p")
    BigDecimal findMinPrice();

    @Query("SELECT MAX(p.price) FROM Product p")
    BigDecimal findMaxPrice();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    long countByPriceBetween(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);

    List<Product> findTop10ByNameContainingOrderByName(String name);

    // 카테고리별 상품 분포 조회
    @Query("SELECT p.category.name, COUNT(p) FROM Product p GROUP BY p.category.name")
    List<Object[]> findProductCategoryDistribution();
}