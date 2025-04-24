package com.peakmeshop.domain.service.impl;

import java.time.LocalDateTime;

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
import com.peakmeshop.api.mapper.ReviewMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional
    public ReviewDTO createReview(ReviewDTO reviewDTO) {
        // 상품 조회
        Product product = productRepository.findById(reviewDTO.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + reviewDTO.getProductId()));

        // 회원 조회
        Member member = memberRepository.findById(reviewDTO.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + reviewDTO.getMemberId()));

        // 리뷰 엔티티 생성
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

        // 리뷰 저장
        Review savedReview = reviewRepository.save(review);

        return reviewMapper.toDTO(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDTO getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + id));

        return reviewMapper.toDTO(review);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable)
                .map(reviewMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsByProductId(Long productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable)
                .map(reviewMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsByMemberId(Long memberId, Pageable pageable) {
        return reviewRepository.findByMemberId(memberId, pageable)
                .map(reviewMapper::toDTO);
    }

    @Override
    @Transactional
    public ReviewDTO updateReview(ReviewDTO reviewDTO) {
        Review review = reviewRepository.findById(reviewDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + reviewDTO.getId()));

        // 리뷰 정보 업데이트
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
        return reviewMapper.toDTO(updatedReview);
    }

    @Override
    @Transactional
    public ReviewDTO incrementHelpfulCount(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + id));

        review.setHelpfulCount(review.getHelpfulCount() + 1);
        review.setUpdatedAt(LocalDateTime.now());

        Review updatedReview = reviewRepository.save(review);
        return reviewMapper.toDTO(updatedReview);
    }

    @Override
    @Transactional
    public ReviewDTO addAdminReply(Long id, String reply) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + id));

        review.setAdminReplied(true);
        review.setAdminReply(reply);
        review.setAdminReplyDate(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        Review updatedReview = reviewRepository.save(review);
        return reviewMapper.toDTO(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + id));

        reviewRepository.delete(review);
    }
}