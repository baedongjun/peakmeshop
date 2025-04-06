package com.peakmeshop.domain.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.peakmeshop.domain.entity.Product;

import java.time.LocalDateTime;

@Repository
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

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.id != :productId")
    List<Product> findByCategoryIdAndIdNot(@Param("categoryId") Long categoryId, @Param("productId") Long productId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId ORDER BY p.salesCount DESC")
    List<Product> findByCategoryIdOrderBySalesCountDesc(@Param("categoryId") Long categoryId, Pageable pageable);

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

    @Query("SELECT p.category.name, COUNT(p) FROM Product p GROUP BY p.category.name")
    List<Object[]> findProductCategoryDistribution();

    long countByIsActiveTrue();
    
    long countByIsFeaturedTrue();
    
    long countByStockLessThanEqual(int stock);

    @Query("SELECT COALESCE(SUM(p.totalSales), 0) FROM Product p")
    BigDecimal calculateTotalSales();
    
    @Query("SELECT COALESCE(AVG(p.rating), 0) FROM Product p WHERE p.rating > 0")
    BigDecimal calculateAverageRating();

    @Query("SELECT p.category.name as category, COUNT(p) as count, SUM(p.totalSales) as total " +
           "FROM Product p WHERE p.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY p.category.name")
    List<Object[]> findSalesByCategory(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p.category.name as category, COUNT(p) as count, SUM(p.price) as total " +
           "FROM Product p WHERE p.category.name = :category " +
           "AND p.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY p.category.name")
    List<Object[]> findSalesByCategory(String category, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT p FROM Product p " +
           "WHERE p.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY p.totalSales DESC")
    Page<Product> findTopProducts(@Param("startDate") LocalDateTime startDate, 
                                @Param("endDate") LocalDateTime endDate, 
                                Pageable pageable);

    @Query("SELECT DATE(p.createdAt) as date, COUNT(p) as count, SUM(p.totalSales) as total " +
           "FROM Product p " +
           "WHERE p.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(p.createdAt)")
    List<Object[]> findSalesTrend(@Param("startDate") LocalDateTime startDate, 
                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p.category.name as category, SUM(p.stock) as totalStock " +
           "FROM Product p " +
           "GROUP BY p.category.name")
    List<Object[]> findStockByCategory();

    @Query("SELECT p FROM Product p WHERE p.stock <= p.lowStockThreshold AND p.stock > 0")
    List<Product> findLowStockProducts();

    @Query("SELECT p FROM Product p WHERE p.stock = 0")
    List<Product> findOutOfStockProducts();

    Optional<Product> findBySlug(String slug);
    
    List<Product> findByIsActiveTrueOrderByNameAsc();
    
    Page<Product> findByIsActiveTrue(Pageable pageable);
}