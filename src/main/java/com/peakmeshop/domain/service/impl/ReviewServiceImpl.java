package com.peakmeshop.domain.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.ReviewDTO;
import com.peakmeshop.domain.entity.Member;
import com.peakmeshop.domain.entity.Product;
import com.peakmeshop.domain.entity.Review;
import com.peakmeshop.domain.repository.MemberRepository;
import com.peakmeshop.domain.repository.ProductRepository;
import com.peakmeshop.domain.repository.ReviewRepository;
import com.peakmeshop.domain.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public ReviewDTO createReview(ReviewDTO reviewDTO) {
        Product product = productRepository.findById(reviewDTO.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));
        
        Member member = memberRepository.findById(reviewDTO.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        Review review = Review.builder()
                .product(product)
                .member(member)
                .rating(reviewDTO.getRating())
                .title(reviewDTO.getTitle())
                .content(reviewDTO.getContent())
                .recommended(false)
                .helpfulCount(0)
                .adminReplied(false)
                .createdAt(LocalDateTime.now())
                .build();

        Review savedReview = reviewRepository.save(review);
        
        // 상품의 평균 평점과 리뷰 수 업데이트
        updateProductRatingAndReviewCount(product.getId());
        
        return convertToDTO(savedReview);
    }

    @Override
    public ReviewDTO getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));
        return convertToDTO(review);
    }

    @Override
    public Page<ReviewDTO> getAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public Page<ReviewDTO> getReviewsByProductId(Long productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public Page<ReviewDTO> getReviewsByMemberId(Long memberId, Pageable pageable) {
        return reviewRepository.findByMemberId(memberId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public ReviewDTO updateReview(ReviewDTO reviewDTO) {
        Review review = reviewRepository.findById(reviewDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));

        review.setRating(reviewDTO.getRating());
        review.setTitle(reviewDTO.getTitle());
        review.setContent(reviewDTO.getContent());
        review.setRecommended(reviewDTO.isRecommended());
        review.setHelpfulCount(reviewDTO.getHelpfulCount());
        review.setAdminReplied(reviewDTO.isAdminReplied());
        review.setAdminReply(reviewDTO.getAdminReply());
        review.setAdminReplyDate(reviewDTO.getAdminReplyDate());
        review.setUpdatedAt(LocalDateTime.now());

        Review updatedReview = reviewRepository.save(review);
        
        // 상품의 평균 평점 업데이트
        updateProductRatingAndReviewCount(review.getProduct().getId());
        
        return convertToDTO(updatedReview);
    }

    @Override
    @Transactional
    public ReviewDTO incrementHelpfulCount(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));
        
        review.setHelpfulCount(review.getHelpfulCount() + 1);
        review.setUpdatedAt(LocalDateTime.now());
        
        Review updatedReview = reviewRepository.save(review);
        return convertToDTO(updatedReview);
    }

    @Override
    @Transactional
    public ReviewDTO addAdminReply(Long id, String reply) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));

        review.setAdminReplied(true);
        review.setAdminReply(reply);
        review.setAdminReplyDate(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        Review updatedReview = reviewRepository.save(review);
        return convertToDTO(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));
        
        Long productId = review.getProduct().getId();
        reviewRepository.delete(review);
        
        // 상품의 평균 평점과 리뷰 수 업데이트
        updateProductRatingAndReviewCount(productId);
    }

    private void updateProductRatingAndReviewCount(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));
        
        double averageRating = reviewRepository.findAverageRatingByProductId(productId);
        int reviewCount = reviewRepository.countByProductId(productId).intValue();
        
        product.setAverageRating(averageRating);
        product.setReviewCount(reviewCount);
        productRepository.save(product);
    }

    private ReviewDTO convertToDTO(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .memberId(review.getMember().getId())
                .memberName(review.getMember().getName())
                .rating(review.getRating())
                .title(review.getTitle())
                .content(review.getContent())
                .recommended(review.isRecommended())
                .helpfulCount(review.getHelpfulCount())
                .adminReplied(review.isAdminReplied())
                .adminReply(review.getAdminReply())
                .adminReplyDate(review.getAdminReplyDate())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    // 추천된 리뷰 조회
    @Override
    public Page<ReviewDTO> getRecommendedReviews(Long productId, Pageable pageable) {
        return reviewRepository.findByProductIdAndRecommendedTrue(productId, pageable)
                .map(this::convertToDTO);
    }

    // 평점별 리뷰 조회
    @Override
    public Page<ReviewDTO> getReviewsByRating(Long productId, Integer rating, Pageable pageable) {
        return reviewRepository.findByProductIdAndRating(productId, rating, pageable)
                .map(this::convertToDTO);
    }

    // 관리자 답변이 있는 리뷰 조회
    @Override
    public Page<ReviewDTO> getRepliedReviews(Long productId, Pageable pageable) {
        return reviewRepository.findByProductIdAndAdminRepliedTrue(productId, pageable)
                .map(this::convertToDTO);
    }

    // 도움이 된 순으로 리뷰 조회
    @Override
    public Page<ReviewDTO> getReviewsByHelpfulCount(Long productId, Pageable pageable) {
        return reviewRepository.findByProductIdOrderByHelpfulCountDesc(productId, pageable)
                .map(this::convertToDTO);
    }

    // 특정 평점 이상의 리뷰 조회
    @Override
    public Page<ReviewDTO> getReviewsByMinRating(Long productId, Integer minRating, Pageable pageable) {
        return reviewRepository.findByProductIdAndRatingGreaterThanEqual(productId, minRating, pageable)
                .map(this::convertToDTO);
    }

    // 주문에 대한 리뷰 존재 여부 확인
    @Override
    @Transactional(readOnly = true)
    public boolean hasOrderReview(Long orderId) {
        return reviewRepository.existsByOrderId(orderId);
    }

    // 사용자의 리뷰 도움이 돼요 여부 확인
    @Override
    public boolean isHelpful(String username, Long reviewId) {
        return reviewRepository.existsByMemberUsernameAndHelpfulReviewId(username, reviewId);
    }

    // 베스트 리뷰 목록 조회
    @Override
    public Page<ReviewDTO> getBestReviews(Long categoryId, Integer minRating, Pageable pageable) {
        if (categoryId != null) {
            if (minRating != null) {
                return reviewRepository.findByCategoryIdAndRatingGreaterThanEqualOrderByHelpfulCountDesc(
                        categoryId, minRating, pageable)
                        .map(this::convertToDTO);
            }
            return reviewRepository.findByCategoryIdOrderByHelpfulCountDesc(categoryId, pageable)
                    .map(this::convertToDTO);
        }
        if (minRating != null) {
            return reviewRepository.findByRatingGreaterThanEqualOrderByHelpfulCountDesc(minRating, pageable)
                    .map(this::convertToDTO);
        }
        return reviewRepository.findAllByOrderByHelpfulCountDesc(pageable)
                .map(this::convertToDTO);
    }

    // 포토 리뷰 목록 조회
    @Override
    public Page<ReviewDTO> getPhotoReviews(Long categoryId, Integer minRating, Pageable pageable) {
        if (categoryId != null) {
            if (minRating != null) {
                return reviewRepository.findByCategoryIdAndRatingGreaterThanEqualAndHasPhotosTrue(
                        categoryId, minRating, pageable)
                        .map(this::convertToDTO);
            }
            return reviewRepository.findByCategoryIdAndHasPhotosTrue(categoryId, pageable)
                    .map(this::convertToDTO);
        }
        if (minRating != null) {
            return reviewRepository.findByRatingGreaterThanEqualAndHasPhotosTrue(minRating, pageable)
                    .map(this::convertToDTO);
        }
        return reviewRepository.findByHasPhotosTrue(pageable)
                .map(this::convertToDTO);
    }

    // 상품의 리뷰 목록 조회 (평점, 사진 여부 필터링)
    @Override
    public Page<ReviewDTO> getProductReviews(Long productId, Integer rating, Boolean hasPhoto, Pageable pageable) {
        if (rating != null && hasPhoto != null) {
            return reviewRepository.findByProductIdAndRatingAndHasPhotos(productId, rating, hasPhoto, pageable)
                    .map(this::convertToDTO);
        } else if (rating != null) {
            return reviewRepository.findByProductIdAndRating(productId, rating, pageable)
                    .map(this::convertToDTO);
        } else if (hasPhoto != null) {
            return reviewRepository.findByProductIdAndHasPhotos(productId, hasPhoto, pageable)
                    .map(this::convertToDTO);
        }
        return reviewRepository.findByProductId(productId, pageable)
                .map(this::convertToDTO);
    }

    // 상품의 리뷰 통계 조회
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getProductReviewStats(Long productId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 전체 리뷰 수
        long totalReviews = reviewRepository.countByProductId(productId);
        stats.put("totalReviews", totalReviews);
        
        // 평균 평점
        Double averageRating = reviewRepository.getAverageRatingByProductId(productId);
        stats.put("averageRating", averageRating != null ? averageRating : 0.0);
        
        // 평점별 리뷰 수
        Map<Integer, Long> ratingDistribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            long count = reviewRepository.countByProductIdAndRating(productId, i);
            ratingDistribution.put(i, count);
        }
        stats.put("ratingDistribution", ratingDistribution);
        
        // 포토 리뷰 수
        long photoReviews = reviewRepository.countByProductIdAndPhotoUrlIsNotNull(productId);
        stats.put("photoReviews", photoReviews);
        
        return stats;
    }
}