package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Map;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);
    Page<Review> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
    Optional<Review> findByMemberIdAndId(Long memberId, Long id);
    void deleteByMemberIdAndId(Long memberId, Long id);
    Page<Review> findByProductIdAndRecommendedTrueOrderByCreatedAtDesc(Long productId, Pageable pageable);
    Page<Review> findByProductIdAndRatingOrderByCreatedAtDesc(Long productId, Integer rating, Pageable pageable);
    Page<Review> findByProductIdAndAdminReplyIsNotNullOrderByCreatedAtDesc(Long productId, Pageable pageable);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double findAverageRatingByProductId(@Param("productId") Long productId);
    
    Long countByProductId(Long productId);
    boolean existsByMemberIdAndProductId(Long memberId, Long productId);
    boolean existsByOrderId(Long orderId);
    
    @Query("SELECT r FROM Review r WHERE r.member.id = :memberId AND r.product.id = :productId")
    Review findByMemberIdAndProductId(@Param("memberId") Long memberId, @Param("productId") Long productId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.member.id = :memberId")
    long countByMemberId(@Param("memberId") Long memberId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.member.id = :memberId")
    Double calculateAverageRatingByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT new map(" +
           "COUNT(r) as totalCount, " +
           "AVG(r.rating) as averageRating, " +
           "SUM(CASE WHEN r.images IS NOT NULL AND SIZE(r.images) > 0 THEN 1 ELSE 0 END) as photoCount, " +
           "SUM(CASE WHEN r.adminReply IS NOT NULL THEN 1 ELSE 0 END) as repliedCount, " +
           "SUM(CASE WHEN r.recommended = true THEN 1 ELSE 0 END) as recommendedCount) " +
           "FROM Review r WHERE r.product.id = :productId")
    Map<String, Object> getProductReviewStats(@Param("productId") Long productId);

    @Query("SELECT r FROM Review r WHERE r.product.id = :productId ORDER BY r.helpfulCount DESC")
    Page<Review> findByProductIdOrderByHelpfulCountDesc(@Param("productId") Long productId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.rating >= :minRating ORDER BY r.createdAt DESC")
    Page<Review> findByProductIdAndRatingGreaterThanEqualOrderByCreatedAtDesc(
            @Param("productId") Long productId, 
            @Param("minRating") Integer minRating, 
            Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.product.category.id = :categoryId AND r.rating >= :minRating ORDER BY r.helpfulCount DESC")
    Page<Review> findByProductCategoryIdAndRatingGreaterThanEqualOrderByHelpfulCountDesc(
            @Param("categoryId") Long categoryId,
            @Param("minRating") Integer minRating,
            Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.product.category.id = :categoryId AND r.rating >= :minRating AND SIZE(r.images) > 0 ORDER BY r.createdAt DESC")
    Page<Review> findByProductCategoryIdAndRatingGreaterThanEqualAndImagesIsNotNullOrderByCreatedAtDesc(
            @Param("categoryId") Long categoryId,
            @Param("minRating") Integer minRating,
            Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.rating = :rating AND " +
           "((:hasPhoto = true AND SIZE(r.images) > 0) OR (:hasPhoto = false AND (r.images IS NULL OR SIZE(r.images) = 0))) " +
           "ORDER BY r.createdAt DESC")
    Page<Review> findByProductIdAndRatingAndImagesIsNotNullOrRatingAndImagesIsNullOrderByCreatedAtDesc(
            @Param("productId") Long productId,
            @Param("rating") Integer rating,
            @Param("hasPhoto") Boolean hasPhoto,
            Pageable pageable);

    @Query("SELECT COUNT(r) > 0 FROM Review r WHERE r.member.id = :memberId AND r.id = :reviewId AND r.helpfulCount > :count")
    boolean existsByMemberIdAndIdAndHelpfulCountGreaterThan(
            @Param("memberId") Long memberId,
            @Param("reviewId") Long reviewId,
            @Param("count") int count);
}