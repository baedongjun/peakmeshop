package com.peakmeshop.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.peakmeshop.dto.MemberDTO;
import com.peakmeshop.dto.ReviewDTO;
import com.peakmeshop.service.MemberService;
import com.peakmeshop.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final MemberService memberService;

    public ReviewController(ReviewService reviewService, MemberService memberService) {
        this.reviewService = reviewService;
        this.memberService = memberService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewDTO> createReview(
            @RequestParam Long productId,
            @RequestParam Integer rating,
            @RequestParam String content,
            @RequestPart(required = false) MultipartFile image,
            Principal principal) {

        MemberDTO member = memberService.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("로그인 정보를 찾을 수 없습니다."));

        ReviewDTO review = reviewService.createReview(member.id(), productId, rating, content, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long id,
            @RequestParam Integer rating,
            @RequestParam String content,
            @RequestPart(required = false) MultipartFile image,
            Principal principal) {

        MemberDTO member = memberService.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("로그인 정보를 찾을 수 없습니다."));

        ReviewDTO review = reviewService.updateReview(id, member.id(), rating, content, image);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            Principal principal) {

        MemberDTO member = memberService.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("로그인 정보를 찾을 수 없습니다."));

        reviewService.deleteReview(id, member.id());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewDTO>> getReviewsByProductId(
            @PathVariable Long productId,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<ReviewDTO> reviews = reviewService.getReviewsByProductId(productId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/member")
    public ResponseEntity<Page<ReviewDTO>> getMyReviews(
            Principal principal,
            @PageableDefault(size = 10) Pageable pageable) {

        MemberDTO member = memberService.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("로그인 정보를 찾을 수 없습니다."));

        Page<ReviewDTO> reviews = reviewService.getReviewsByMemberId(member.id(), pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/product/{productId}/rating")
    public ResponseEntity<Double> getProductAverageRating(@PathVariable Long productId) {
        Double averageRating = reviewService.calculateAverageRating(productId);
        return ResponseEntity.ok(averageRating);
    }
}