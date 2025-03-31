package com.peakmeshop.api.controller;

import com.peakmeshop.api.dto.ReviewDTO;
import com.peakmeshop.common.security.oauth2.user.UserPrincipal;
import com.peakmeshop.domain.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<Page<ReviewDTO>> getAllReviews(Pageable pageable) {
        Page<ReviewDTO> reviews = reviewService.getAllReviews(pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long id) {
        ReviewDTO reviewDTO = reviewService.getReviewById(id);
        return ResponseEntity.ok(reviewDTO);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewDTO>> getReviewsByProductId(
            @PathVariable Long productId, Pageable pageable) {
        Page<ReviewDTO> reviews = reviewService.getReviewsByProductId(productId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<Page<ReviewDTO>> getReviewsByMemberId(
            @PathVariable Long memberId, Pageable pageable, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        // 일반 사용자는 자신의 리뷰만 조회 가능
        if (!userPrincipal.isAdmin() && !userPrincipal.getId().equals(memberId)) {
            return ResponseEntity.status(403).build();
        }

        Page<ReviewDTO> reviews = reviewService.getReviewsByMemberId(memberId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<ReviewDTO>> getMyReviews(
            Pageable pageable, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Page<ReviewDTO> reviews = reviewService.getReviewsByMemberId(userPrincipal.getId(), pageable);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReviewDTO> createReview(
            @Valid @RequestBody ReviewDTO reviewDTO, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        // 사용자는 자신의 리뷰만 작성 가능
        reviewDTO.setMemberId(userPrincipal.getId());

        ReviewDTO createdReview = reviewService.createReview(reviewDTO);
        return ResponseEntity.ok(createdReview);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long id, @Valid @RequestBody ReviewDTO reviewDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ReviewDTO existingReview = reviewService.getReviewById(id);

        // 일반 사용자는 자신의 리뷰만 수정 가능
        if (!userPrincipal.isAdmin() && !userPrincipal.getId().equals(existingReview.getMemberId())) {
            return ResponseEntity.status(403).build();
        }

        // 관리자가 아닌 경우, 관리자 관련 필드는 수정 불가
        if (!userPrincipal.isAdmin()) {
            reviewDTO.setRecommended(existingReview.isRecommended());
            reviewDTO.setHelpfulCount(existingReview.getHelpfulCount());
            reviewDTO.setAdminReplied(existingReview.isAdminReplied());
            reviewDTO.setAdminReply(existingReview.getAdminReply());
            reviewDTO.setAdminReplyDate(existingReview.getAdminReplyDate());
        }

        reviewDTO.setId(id);
        ReviewDTO updatedReview = reviewService.updateReview(reviewDTO);
        return ResponseEntity.ok(updatedReview);
    }

    @PutMapping("/{id}/helpful")
    public ResponseEntity<ReviewDTO> incrementHelpfulCount(@PathVariable Long id) {
        ReviewDTO updatedReview = reviewService.incrementHelpfulCount(id);
        return ResponseEntity.ok(updatedReview);
    }

    @PutMapping("/{id}/admin-reply")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReviewDTO> addAdminReply(
            @PathVariable Long id, @RequestParam String reply) {
        ReviewDTO updatedReview = reviewService.addAdminReply(id, reply);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ReviewDTO reviewDTO = reviewService.getReviewById(id);

        // 일반 사용자는 자신의 리뷰만 삭제 가능
        if (!userPrincipal.isAdmin() && !userPrincipal.getId().equals(reviewDTO.getMemberId())) {
            return ResponseEntity.status(403).build();
        }

        reviewService.deleteReview(id);
        return ResponseEntity.ok().build();
    }
}