package com.peakmeshop.domain.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.peakmeshop.domain.entity.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findBySlug(String slug);

    List<Brand> findByIsActiveTrueOrderByNameAsc();

    List<Brand> findByIsFeaturedTrueAndIsActiveTrueOrderByNameAsc();

    Page<Brand> findByIsActiveTrue(Pageable pageable);

    Page<Brand> findByIsFeaturedTrue(Pageable pageable);

    boolean existsBySlug(String slug);

    boolean existsByName(String name);

    @Query("SELECT b FROM Brand b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Brand> searchBrands(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Brand b LEFT JOIN b.products p GROUP BY b.id ORDER BY COUNT(p) DESC")
    List<Brand> findTopBrandsByProductCount(Pageable pageable);

    @Query("SELECT COUNT(p) FROM Brand b JOIN b.products p WHERE b.id = :brandId")
    int countProductsByBrandId(@Param("brandId") Long brandId);

    long countByIsActiveTrue();

    long countByIsFeaturedTrue();

    @Query("SELECT COUNT(p) FROM Brand b JOIN b.products p")
    long countTotalProducts();

    @Query("SELECT COALESCE(SUM(oi.price * oi.quantity), 0) FROM Brand b JOIN b.products p JOIN p.orderItems oi")
    BigDecimal calculateTotalSales();

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Brand b JOIN b.products p JOIN p.reviews r")
    Double calculateAverageRating();

    @Query("SELECT COALESCE(SUM(oi.price * oi.quantity), 0) FROM Brand b JOIN b.products p JOIN p.orderItems oi WHERE b.id = :brandId")
    BigDecimal calculateTotalSalesByBrandId(@Param("brandId") Long brandId);

    @Query("SELECT COUNT(oi) FROM Brand b JOIN b.products p JOIN p.orderItems oi WHERE b.id = :brandId")
    Long countOrdersByBrandId(@Param("brandId") Long brandId);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Brand b JOIN b.products p JOIN p.reviews r WHERE b.id = :brandId")
    Double calculateAverageRatingByBrandId(@Param("brandId") Long brandId);

    @Query("SELECT COUNT(oi) FROM Brand b JOIN b.products p JOIN p.orderItems oi WHERE b.id = :brandId AND oi.createdAt BETWEEN :startDate AND :endDate")
    Long countOrdersByBrandIdAndDateRange(@Param("brandId") Long brandId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(oi.price * oi.quantity), 0) FROM Brand b JOIN b.products p JOIN p.orderItems oi WHERE b.id = :brandId AND oi.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalSalesByBrandIdAndDateRange(@Param("brandId") Long brandId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(b) FROM Brand b WHERE b.createdAt BETWEEN :startOfMonth AND :endOfMonth")
    Long countMonthlyNewBrands(@Param("startOfMonth") LocalDateTime startOfMonth, @Param("endOfMonth") LocalDateTime endOfMonth);

    @Query("SELECT COUNT(b) FROM Brand b WHERE b.createdAt < :startDate")
    long countBrandsBeforePeriod(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(b) FROM Brand b WHERE b.createdAt BETWEEN :startDate AND :endDate")
    long countBrandsInPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new map(DATE(b.createdAt) as date, COUNT(b) as count) FROM Brand b WHERE b.createdAt BETWEEN :startDate AND :endDate GROUP BY DATE(b.createdAt)")
    List<Map<String, Object>> getDailyNewBrands(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new map(FUNCTION('YEARWEEK', b.createdAt) as week, COUNT(b) as count) FROM Brand b WHERE b.createdAt BETWEEN :startDate AND :endDate GROUP BY FUNCTION('YEARWEEK', b.createdAt)")
    List<Map<String, Object>> getWeeklyNewBrands(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new map(FUNCTION('YEAR', b.createdAt) as year, FUNCTION('MONTH', b.createdAt) as month, COUNT(b) as count) FROM Brand b WHERE b.createdAt BETWEEN :startDate AND :endDate GROUP BY FUNCTION('YEAR', b.createdAt), FUNCTION('MONTH', b.createdAt)")
    List<Map<String, Object>> getMonthlyNewBrands(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new map(FUNCTION('YEAR', b.createdAt) as year, COUNT(b) as count) FROM Brand b WHERE b.createdAt BETWEEN :startDate AND :endDate GROUP BY FUNCTION('YEAR', b.createdAt)")
    List<Map<String, Object>> getYearlyNewBrands(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new map(DATE(b.createdAt) as date, COUNT(b) as count) FROM Brand b WHERE b.createdAt BETWEEN :startDate AND :endDate GROUP BY DATE(b.createdAt)")
    List<Map<String, Object>> getCustomPeriodNewBrands(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new map(DATE(oi.createdAt) as date, SUM(oi.price * oi.quantity) as sales) FROM Brand b JOIN b.products p JOIN p.orderItems oi WHERE oi.createdAt BETWEEN :startDate AND :endDate GROUP BY DATE(oi.createdAt)")
    List<Map<String, Object>> getDailySales(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new map(FUNCTION('YEARWEEK', oi.createdAt) as week, SUM(oi.price * oi.quantity) as sales) FROM Brand b JOIN b.products p JOIN p.orderItems oi WHERE oi.createdAt BETWEEN :startDate AND :endDate GROUP BY FUNCTION('YEARWEEK', oi.createdAt)")
    List<Map<String, Object>> getWeeklySales(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new map(FUNCTION('YEAR', oi.createdAt) as year, FUNCTION('MONTH', oi.createdAt) as month, SUM(oi.price * oi.quantity) as sales) FROM Brand b JOIN b.products p JOIN p.orderItems oi WHERE oi.createdAt BETWEEN :startDate AND :endDate GROUP BY FUNCTION('YEAR', oi.createdAt), FUNCTION('MONTH', oi.createdAt)")
    List<Map<String, Object>> getMonthlySales(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new map(FUNCTION('YEAR', oi.createdAt) as year, SUM(oi.price * oi.quantity) as sales) FROM Brand b JOIN b.products p JOIN p.orderItems oi WHERE oi.createdAt BETWEEN :startDate AND :endDate GROUP BY FUNCTION('YEAR', oi.createdAt)")
    List<Map<String, Object>> getYearlySales(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new map(DATE(oi.createdAt) as date, SUM(oi.price * oi.quantity) as sales) FROM Brand b JOIN b.products p JOIN p.orderItems oi WHERE oi.createdAt BETWEEN :startDate AND :endDate GROUP BY DATE(oi.createdAt)")
    List<Map<String, Object>> getCustomPeriodSales(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new map(b.name as brandName, SUM(oi.price * oi.quantity) as revenue) FROM Brand b JOIN b.products p JOIN p.orderItems oi WHERE oi.createdAt BETWEEN :startDate AND :endDate GROUP BY b.id, b.name ORDER BY revenue DESC")
    List<Map<String, Object>> getTopBrandsByRevenue(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("limit") int limit);

    @Query("SELECT new map(c.name as category, COUNT(DISTINCT b.id) as brandCount) FROM Brand b JOIN b.products p JOIN p.category c WHERE b.createdAt BETWEEN :startDate AND :endDate GROUP BY c.id, c.name")
    List<Map<String, Object>> getBrandCategoriesDistribution(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}

