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
import com.peakmeshop.domain.entity.Category;

import java.time.LocalDateTime;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByCode(String code);

    Optional<Product> findBySlug(String slug);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findByBrand(String brand, Pageable pageable);

    Page<Product> findByBrandId(Long brandId, Pageable pageable);

    Page<Product> findByIsActiveTrue(Pageable pageable);

    Page<Product> findByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable);

    Page<Product> findByNameContainingOrDescriptionContainingAndCategoryId(String name, String description, Long categoryId, Pageable pageable);

    Page<Product> findByNameContaining(String query, Pageable pageable);

    Page<Product> findByIsFeaturedTrueAndIsActiveTrue(Pageable pageable);

    Page<Product> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<Product> findByIsActiveTrueOrderBySalesCountDesc(Pageable pageable);

    Page<Product> findByIsActiveTrueAndSalePriceIsNotNullOrderBySalePriceAsc(Pageable pageable);

    List<Product> findTop10ByIsActiveTrueOrderBySalesCountDesc();

    long countByCategoryId(Long categoryId);

    long countByStatus(String status);

    long countByStockLessThanAndStockGreaterThan(int maxStock, int minStock);

    long countByStockGreaterThan(Integer stock);

    long countByStockEquals(Integer stock);

    long countByStockLessThanEqual(int stock);

    long countByIsActiveTrue();

    long countByIsActiveFalse();

    long countByIsFeaturedTrue();

    Page<Product> findByStockGreaterThan(Integer stock, Pageable pageable);

    Page<Product> findByStockEquals(Integer stock, Pageable pageable);

    Page<Product> findByStockLessThanEqual(int stock, Pageable pageable);

    Page<Product> findByStockLessThanEqualAndIsActive(int stock, boolean isActive, Pageable pageable);

    List<Product> findLowStockProducts();

    List<Product> findOutOfStockProducts();

    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT MIN(p.price) FROM Product p")
    BigDecimal findMinPrice();

    @Query("SELECT MAX(p.price) FROM Product p")
    BigDecimal findMaxPrice();

    @Query("SELECT COALESCE(SUM(p.salePrice), 0) FROM Product p")
    BigDecimal calculateTotalSales();

    @Query("SELECT COALESCE(AVG(p.averageRating), 0) FROM Product p WHERE p.averageRating > 0")
    BigDecimal calculateAverageRating();

    @Query("SELECT COALESCE(AVG(p.price), 0) FROM Product p WHERE p.isActive = true")
    double calculateAveragePrice();

    @Query("SELECT COALESCE(SUM(p.stock), 0) FROM Product p WHERE p.isActive = true")
    long calculateTotalInventory();

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.brand.id IN :brandIds AND p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByCategoryIdAndBrandIdInAndPriceBetween(
            @Param("categoryId") Long categoryId,
            @Param("brandIds") List<Long> brandIds,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.brand.id = :brandId AND p.category.id = :categoryId AND p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByBrandIdAndCategoryIdAndPriceBetween(
            @Param("brandId") Long brandId,
            @Param("categoryId") Long categoryId,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.id != :productId ORDER BY p.viewCount DESC")
    List<Product> findByCategoryIdAndIdNot(@Param("categoryId") Long categoryId, @Param("productId") Long productId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.brand.id IN :brandIds AND p.createdAt BETWEEN :startDate AND :endDate ORDER BY p.salesCount DESC")
    Page<Product> findBestSellersByCategoryAndBrandsAndPeriod(
            @Param("categoryId") Long categoryId,
            @Param("brandIds") List<Long> brandIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.brand.id IN :brandIds AND p.createdAt > :date")
    Page<Product> findByCategoryIdAndBrandIdInAndCreatedAtAfter(
            @Param("categoryId") Long categoryId,
            @Param("brandIds") List<Long> brandIds,
            @Param("date") LocalDateTime date,
            Pageable pageable);

    Page<Product> findByCategory(Category category, Pageable pageable);

    Page<Product> findByCategoryAndIdNot(Category category, Long productId, Pageable pageable);

    @Query(value = """
            SELECT p FROM Product p
            WHERE p.id IN (
                SELECT oi2.product.id
                FROM OrderItem oi1
                JOIN OrderItem oi2 ON oi1.order = oi2.order
                WHERE oi1.product.id = :productId
                AND oi2.product.id != :productId
                GROUP BY oi2.product.id
                ORDER BY COUNT(oi2.product.id) DESC
            )
            """)
    Page<Product> findFrequentlyBoughtTogether(@Param("productId") Long productId, Pageable pageable);

    @Query(value = """
            SELECT p FROM Product p
            WHERE (:categoryId IS NULL OR p.category.id = :categoryId)
            AND (:brandId IS NULL OR p.brand.id = :brandId)
            AND (:minPrice IS NULL OR p.price >= :minPrice)
            AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            AND (:inStock IS NULL OR (CASE WHEN p.stockQuantity > 0 THEN true ELSE false END) = :inStock)
            """)
    Page<Product> findByFilters(
            @Param("categoryId") Long categoryId,
            @Param("brandId") Long brandId,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("inStock") Boolean inStock,
            Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.category.id = :categoryId ORDER BY p.createdAt DESC")
    Page<Product> findActiveByCategoryIdOrderByCreatedAtDesc(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.brand.id = :brandId ORDER BY p.createdAt DESC")
    Page<Product> findActiveByBrandIdOrderByCreatedAtDesc(@Param("brandId") Long brandId, Pageable pageable);

    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.category.id = :categoryId AND p.isActive = true")
    List<String> findDistinctBrandsByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.salePrice IS NOT NULL AND p.salePrice > 0 ORDER BY (p.price - p.salePrice)/p.price DESC")
    Page<Product> findActiveProductsWithHighestDiscountPercentage(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.averageRating >= :minRating ORDER BY p.averageRating DESC")
    Page<Product> findActiveByMinimumRating(@Param("minRating") Double minRating, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.tags LIKE %:tag% ORDER BY p.createdAt DESC")
    Page<Product> findActiveByTag(@Param("tag") String tag, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND FUNCTION('MONTH', p.createdAt) = :month AND FUNCTION('YEAR', p.createdAt) = :year")
    Page<Product> findActiveByMonthAndYear(@Param("month") int month, @Param("year") int year, Pageable pageable);

    @Query(value = """
            SELECT DISTINCT p.* FROM products p
            LEFT JOIN order_items oi ON p.id = oi.product_id
            LEFT JOIN orders o ON oi.order_id = o.id
            WHERE o.member_id = :memberId
            AND p.status = 'ACTIVE'
            ORDER BY 
                CASE 
                    WHEN o.created_at IS NOT NULL THEN 1
                    ELSE 2
                END,
                o.created_at DESC
            LIMIT 10
            """, nativeQuery = true)
    List<Product> findRecommendedProducts(@Param("memberId") Long memberId);

    List<Product> findByNameContainingIgnoreCase(String keyword);

    Page<Product> findByStatus(String status, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
           "(:category IS NULL OR p.category.id = :category) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> findProducts(@Param("category") Long category,
                             @Param("status") String status,
                             @Param("keyword") String keyword,
                             Pageable pageable);

}