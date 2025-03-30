package com.peakmeshop.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peakmeshop.entity.ProductView;

public interface ProductViewRepository extends JpaRepository<ProductView, Long> {

    @Query("SELECT pv FROM ProductView pv WHERE pv.product.id = :productId AND pv.viewDate >= :startDate AND pv.viewDate <= :endDate")
    List<ProductView> findByProductIdAndDateRange(
            @Param("productId") Long productId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT pv FROM ProductView pv WHERE pv.member.id = :memberId ORDER BY pv.viewDate DESC")
    Page<ProductView> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT pv FROM ProductView pv WHERE pv.sessionId = :sessionId ORDER BY pv.viewDate DESC")
    Page<ProductView> findBySessionId(@Param("sessionId") String sessionId, Pageable pageable);

    @Query("SELECT COUNT(pv) FROM ProductView pv WHERE pv.product.id = :productId AND pv.viewDate >= :startDate AND pv.viewDate <= :endDate")
    long countByProductIdAndDateRange(
            @Param("productId") Long productId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT pv.product.id, COUNT(pv) as viewCount FROM ProductView pv " +
            "WHERE pv.viewDate >= :startDate AND pv.viewDate <= :endDate " +
            "GROUP BY pv.product.id ORDER BY viewCount DESC")
    List<Object[]> findMostViewedProducts(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT pv FROM ProductView pv WHERE pv.member.id = :memberId AND pv.product.id = :productId " +
            "ORDER BY pv.viewDate DESC")
    List<ProductView> findByMemberIdAndProductId(
            @Param("memberId") Long memberId,
            @Param("productId") Long productId,
            Pageable pageable);

    @Query("SELECT pv FROM ProductView pv WHERE pv.sessionId = :sessionId AND pv.product.id = :productId " +
            "ORDER BY pv.viewDate DESC")
    List<ProductView> findBySessionIdAndProductId(
            @Param("sessionId") String sessionId,
            @Param("productId") Long productId,
            Pageable pageable);


    // 최근 본 상품 ID 조회
    @Query(value = "SELECT product_id FROM product_views " +
            "WHERE member_id = :memberId " +
            "ORDER BY viewed_at DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<Long> findRecentlyViewedProductIds(@Param("memberId") Long memberId, @Param("limit") int limit);

    // 상품 조회 기록 저장
    @Modifying
    @Query(value = "INSERT INTO product_views (product_id, member_id, viewed_at) " +
            "VALUES (:productId, :memberId, NOW()) " +
            "ON DUPLICATE KEY UPDATE viewed_at = NOW()", nativeQuery = true)
    void saveProductView(@Param("productId") Long productId, @Param("memberId") Long memberId);
}