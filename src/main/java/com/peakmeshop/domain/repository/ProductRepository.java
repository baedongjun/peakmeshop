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

    Page<Product> findByIsFeaturedTrueAndIsActiveTrue(Pageable pageable);

    Page<Product> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<Product> findByIsActiveTrueOrderBySalesCountDesc(Pageable pageable);

    Page<Product> findByIsActiveTrueAndSalePriceIsNotNullOrderBySalePriceAsc(Pageable pageable);

    List<Product> findTop10ByIsActiveTrueOrderBySalesCountDesc();

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
    long countByIsActiveFalse();

    long countByIsFeaturedTrue();
    
    long countByStockLessThanEqual(int stock);

    @Query("SELECT COALESCE(SUM(p.salePrice), 0) FROM Product p")
    BigDecimal calculateTotalSales();
    
    @Query("SELECT COALESCE(AVG(p.averageRating), 0) FROM Product p WHERE p.averageRating > 0")
    BigDecimal calculateAverageRating();

    @Query("SELECT p.category.name as category, COUNT(p) as count, SUM(p.price) as total " +
           "FROM Product p WHERE p.category.name = :category " +
           "AND p.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY p.category.name")
    List<Object[]> findSalesByCategory(String category, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT p.category.name as category, SUM(p.stock) as totalStock " +
           "FROM Product p " +
           "GROUP BY p.category.name")
    List<Object[]> findStockByCategory();

    @Query("SELECT p FROM Product p WHERE p.stock <= p.stockAlert AND p.stock > 0")
    List<Product> findLowStockProducts();

    @Query("SELECT p FROM Product p WHERE p.stock = 0")
    List<Product> findOutOfStockProducts();

    Optional<Product> findBySlug(String slug);
    
    List<Product> findByIsActiveTrueOrderByNameAsc();
    
    Page<Product> findByIsActiveTrue(Pageable pageable);

    List<Product> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    long countByStatus(String status);

    long countByStockLessThanAndStockGreaterThan(int maxStock, int minStock);

    @Query("SELECT COALESCE(AVG(p.price), 0) FROM Product p WHERE p.isActive = true")
    double calculateAveragePrice();

    @Query("SELECT COALESCE(SUM(p.stock), 0) FROM Product p WHERE p.isActive = true")
    long calculateTotalInventory();

    @Query("SELECT COALESCE(CAST(SUM(o.quantity) AS double) / " +
           "NULLIF(AVG(p.stock), 0), 0) " +
           "FROM OrderItem o " +
           "JOIN o.product p " +
           "WHERE o.createdAt BETWEEN :startDate AND :endDate")
    double calculateInventoryTurnover(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COUNT(DISTINCT p) FROM OrderItem o JOIN o.product p " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "AND (SELECT COUNT(oi) FROM OrderItem oi WHERE oi.product = p) >= 10")
    long countTopSellers(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p.category.name, " +
           "COUNT(o) as count, " +
           "COALESCE(SUM(o.price * o.quantity), 0) as total " +
           "FROM OrderItem o " +
           "JOIN o.product p " +
           "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY p.category.name " +
           "ORDER BY total DESC")
    List<Object[]> findSalesByCategory(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT cast(o.createdAt as date) as date, " +
           "COUNT(o) as count, " +
           "COALESCE(SUM(o.price * o.quantity), 0) as total " +
           "FROM OrderItem o " +
           "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY cast(o.createdAt as date) " +
           "ORDER BY date")
    List<Object[]> findSalesTrend(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Product p " +
           "WHERE EXISTS (SELECT o FROM OrderItem o WHERE o.product = p " +
           "AND o.createdAt BETWEEN :startDate AND :endDate) " +
           "ORDER BY p.salesCount DESC")
    Page<Product> findTopProducts(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate,
                                 Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE EXISTS (SELECT o FROM OrderItem o WHERE o.product = p " +
            "AND o.createdAt BETWEEN :startDate AND :endDate) " +
            "ORDER BY p.salesCount DESC")
    List<Product> findTopProducts(@Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate);

    /**
     * 재고가 특정 수량 이하인 상품을 조회합니다.
     * @param stock 재고 수량
     * @param pageable 페이징 정보
     * @return 재고가 부족한 상품 목록
     */
    Page<Product> findByStockLessThanEqual(int stock, Pageable pageable);

    /**
     * 재고가 특정 수량 이하이고 활성화된 상품을 조회합니다.
     * @param stock 재고 수량
     * @param isActive 활성화 여부
     * @param pageable 페이징 정보
     * @return 재고가 부족한 활성화된 상품 목록
     */
    Page<Product> findByStockLessThanEqualAndIsActive(int stock, boolean isActive, Pageable pageable);

}