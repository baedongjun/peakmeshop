package com.peakmeshop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.peakmeshop.dto.ReviewDTO;

public interface ReviewService {

    /**
     * 리뷰 작성
     * @param memberId 회원 ID
     * @param productId 상품 ID
     * @param rating 평점
     * @param content 내용
     * @param image 이미지 (선택적)
     * @return 작성된 리뷰
     */
    ReviewDTO createReview(Long memberId, Long productId, Integer rating, String content, MultipartFile image);

    /**
     * 리뷰 수정
     * @param reviewId 리뷰 ID
     * @param memberId 회원 ID
     * @param rating 평점
     * @param content 내용
     * @param image 이미지 (선택적)
     * @return 수정된 리뷰
     */
    ReviewDTO updateReview(Long reviewId, Long memberId, Integer rating, String content, MultipartFile image);

    /**
     * 리뷰 삭제
     * @param reviewId 리뷰 ID
     * @param memberId 회원 ID
     */
    void deleteReview(Long reviewId, Long memberId);

    /**
     * 리뷰 ID로 조회
     * @param reviewId 리뷰 ID
     * @return 리뷰 정보
     */
    Optional<ReviewDTO> getReviewById(Long reviewId);

    /**
     * 상품 ID로 리뷰 목록 조회
     * @param productId 상품 ID
     * @param pageable 페이징 정보
     * @return 리뷰 목록
     */
    Page<ReviewDTO> getReviewsByProductId(Long productId, Pageable pageable);

    /**
     * 회원 ID로 리뷰 목록 조회
     * @param memberId 회원 ID
     * @param pageable 페이징 정보
     * @return 리뷰 목록
     */
    Page<ReviewDTO> getReviewsByMemberId(Long memberId, Pageable pageable);

    /**
     * 상품 평균 평점 계산
     * @param productId 상품 ID
     * @return 평균 평점
     */
    Double calculateAverageRating(Long productId);
}