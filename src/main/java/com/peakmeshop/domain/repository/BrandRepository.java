package com.peakmeshop.domain.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.peakmeshop.domain.entity.Brand;
import com.peakmeshop.domain.entity.Category;
import com.peakmeshop.domain.entity.Member;

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

    @Query("SELECT coalesce(SUM(oi.price * oi.quantity),0) FROM Brand b JOIN b.products p JOIN p.orderItems oi")
    BigDecimal calculateTotalSales();

    @Query("SELECT coalesce(AVG(r.rating),0) FROM Brand b JOIN b.products p JOIN p.reviews r")
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

    /**
     * 이번 달에 등록된 신규 브랜드 수를 조회합니다.
     *
     * @param startOfMonth 이번 달의 시작일
     * @param endOfMonth 이번 달의 마지막일
     * @return 이번 달 신규 등록된 브랜드 수
     */
    @Query("SELECT COUNT(b) FROM Brand b WHERE b.createdAt BETWEEN :startOfMonth AND :endOfMonth")
    Long countMonthlyNewBrands(LocalDateTime startOfMonth, LocalDateTime endOfMonth);

    List<Brand> findByNameContainingIgnoreCase(String name);

    Page<Brand> findByCategoryAndNameContainingIgnoreCase(String category, String name, Pageable pageable);

    Page<Brand> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT DISTINCT b.category FROM Brand b WHERE b.category IS NOT NULL ORDER BY b.category")
    List<String> findDistinctCategories();

    @Query("SELECT b FROM Brand b WHERE b.category.code = :category AND " +
           "(LOWER(b.name) LIKE %:keyword% OR LOWER(b.description) LIKE %:keyword%)")
    Page<Brand> findByCategoryAndKeyword(@Param("category") String category, 
                                       @Param("keyword") String keyword, 
                                       Pageable pageable);

    @Query("SELECT b FROM Brand b WHERE b.category.code = :category")
    Page<Brand> findByCategory(@Param("category") String category, Pageable pageable);

    @Query("SELECT b FROM Brand b WHERE LOWER(b.name) LIKE %:keyword% OR " +
           "LOWER(b.description) LIKE %:keyword%")
    Page<Brand> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT DISTINCT c FROM Category c JOIN c.brands b")
    List<Category> findAllCategories();

    @Modifying
    @Query("UPDATE Brand b SET b.viewCount = b.viewCount + 1 WHERE b.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Brand b " +
           "JOIN b.followers f WHERE f = :member AND b.id = :brandId")
    boolean existsByFollowerAndId(@Param("member") Member member, @Param("brandId") Long brandId);
}

