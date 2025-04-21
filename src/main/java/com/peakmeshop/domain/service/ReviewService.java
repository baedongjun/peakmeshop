package com.peakmeshop.domain.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.ReviewDTO;

import java.util.Map;

public interface ReviewService {
    
    ReviewDTO createReview(String userId, ReviewDTO reviewDTO);
    
    ReviewDTO getReviewById(Long id);
    
    Page<ReviewDTO> getAllReviews(Pageable pageable);
    
    Page<ReviewDTO> getReviewsByProductId(Long productId, Pageable pageable);
    
    Page<ReviewDTO> getReviewsByUserId(String userId, Pageable pageable);
    
    ReviewDTO updateReview(String userId, Long reviewId, ReviewDTO reviewDTO);
    
    ReviewDTO incrementHelpfulCount(Long id);
    
    ReviewDTO addAdminReply(Long id, String reply);
    
    void deleteReview(String userId, Long reviewId);
    
    Page<ReviewDTO> getRecommendedReviews(Long productId, Pageable pageable);
    
    Page<ReviewDTO> getReviewsByRating(Long productId, Integer rating, Pageable pageable);
    
    Page<ReviewDTO> getRepliedReviews(Long productId, Pageable pageable);
    
    Page<ReviewDTO> getReviewsByHelpfulCount(Long productId, Pageable pageable);
    
    Page<ReviewDTO> getReviewsByMinRating(Long productId, Integer minRating, Pageable pageable);
    
    boolean hasOrderReview(Long orderId);
    
    boolean isHelpful(String username, Long reviewId);
    
    Page<ReviewDTO> getBestReviews(Long categoryId, Integer minRating, Pageable pageable);
    
    Page<ReviewDTO> getPhotoReviews(Long categoryId, Integer minRating, Pageable pageable);
    
    Page<ReviewDTO> getProductReviews(Long productId, Integer rating, Boolean hasPhoto, Pageable pageable);
    
    Map<String, Object> getProductReviewStats(Long productId);
    
    long getReviewCountByUserId(String userId);
    
    Double getAverageRatingByUserId(String userId);
    
    ReviewDTO getReviewByUserIdAndReviewId(String userId, Long reviewId);
    
    ReviewDTO getReviewByUserIdAndProductId(String userId, Long productId);
}