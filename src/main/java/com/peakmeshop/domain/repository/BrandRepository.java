package com.peakmeshop.domain.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
    Page<Brand> searchBrands(String keyword, Pageable pageable);

    @Query("SELECT b FROM Brand b LEFT JOIN b.products p GROUP BY b.id ORDER BY COUNT(p) DESC")
    List<Brand> findTopBrandsByProductCount(Pageable pageable);

    @Query("SELECT COUNT(p) FROM Brand b JOIN b.products p WHERE b.id = :brandId")
    int countProductsByBrandId(Long brandId);

    long countByIsActiveTrue();

    long countByIsFeaturedTrue();

    @Query("SELECT COUNT(p) FROM Brand b JOIN b.products p")
    long countTotalProducts();

    @Query("SELECT SUM(oi.price * oi.quantity) FROM Brand b JOIN b.products p JOIN p.orderItems oi")
    BigDecimal calculateTotalSales();

    @Query("SELECT AVG(r.rating) FROM Brand b JOIN b.products p JOIN p.reviews r")
    Double calculateAverageRating();

    @Query("SELECT SUM(oi.price * oi.quantity) FROM Brand b JOIN b.products p JOIN p.orderItems oi WHERE b.id = :brandId")
    BigDecimal calculateTotalSalesByBrandId(@Param("brandId") Long brandId);

    @Query("SELECT COUNT(oi) FROM Brand b JOIN b.products p JOIN p.orderItems oi WHERE b.id = :brandId")
    Long countOrdersByBrandId(@Param("brandId") Long brandId);

    @Query("SELECT AVG(r.rating) FROM Brand b JOIN b.products p JOIN p.reviews r WHERE b.id = :brandId")
    Double calculateAverageRatingByBrandId(@Param("brandId") Long brandId);

    @Query("SELECT COUNT(oi) FROM Brand b JOIN b.products p JOIN p.orderItems oi WHERE b.id = :brandId AND oi.createdAt BETWEEN :startDate AND :endDate")
    Long countOrdersByBrandIdAndDateRange(@Param("brandId") Long brandId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(oi.price * oi.quantity) FROM Brand b JOIN b.products p JOIN p.orderItems oi WHERE b.id = :brandId AND oi.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalSalesByBrandIdAndDateRange(@Param("brandId") Long brandId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}

