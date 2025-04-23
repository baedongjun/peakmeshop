package com.peakmeshop.domain.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.ReviewDTO;

public interface ReviewService {

    // 리뷰 생성
    ReviewDTO createReview(ReviewDTO reviewDTO);

    // 리뷰 조회
    ReviewDTO getReviewById(Long id);

    // 모든 리뷰 조회
    Page<ReviewDTO> getAllReviews(Pageable pageable);

    // 상품별 리뷰 조회
    Page<ReviewDTO> getReviewsByProductId(Long productId, Pageable pageable);

    // 회원별 리뷰 조회
    Page<ReviewDTO> getReviewsByMemberId(Long memberId, Pageable pageable);

    // 리뷰 수정
    ReviewDTO updateReview(ReviewDTO reviewDTO);

    // 도움이 됐어요 수 증가
    ReviewDTO incrementHelpfulCount(Long id);

    // 관리자 답변 추가
    ReviewDTO addAdminReply(Long id, String reply);

    // 리뷰 삭제
    void deleteReview(Long id);
}