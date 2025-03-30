package com.peakmeshop.controller;

import com.peakmeshop.dto.ProductDTO;
import com.peakmeshop.dto.RecommendationDTO;
import com.peakmeshop.security.oauth2.user.UserPrincipal;
import com.peakmeshop.service.RecommendationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/personalized")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductDTO>> getPersonalizedRecommendations(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendationService.getPersonalizedRecommendations(userPrincipal.getId(), limit));
    }

    @GetMapping("/similar/{productId}")
    public ResponseEntity<List<ProductDTO>> getSimilarProducts(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendationService.getSimilarProducts(productId, limit));
    }

    @GetMapping("/frequently-bought-together/{productId}")
    public ResponseEntity<List<ProductDTO>> getFrequentlyBoughtTogether(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendationService.getFrequentlyBoughtTogether(productId, limit));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ProductDTO>> getPopularProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendationService.getPopularProducts(limit));
    }

    @GetMapping("/new-arrivals")
    public ResponseEntity<List<ProductDTO>> getNewArrivals(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendationService.getNewArrivals(limit));
    }

    @GetMapping("/best-sellers")
    public ResponseEntity<List<ProductDTO>> getBestSellers(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendationService.getBestSellers(limit));
    }

    @GetMapping("/on-sale")
    public ResponseEntity<List<ProductDTO>> getOnSaleProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendationService.getOnSaleProducts(limit));
    }

    @GetMapping("/recently-viewed")
    public ResponseEntity<List<ProductDTO>> getRecentlyViewedProducts(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "10") int limit) {
        Long memberId = userPrincipal != null ? userPrincipal.getId() : null;
        if (memberId == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(recommendationService.getRecentlyViewedProducts(memberId, limit));
    }

    @PostMapping("/record-view/{productId}")
    public ResponseEntity<Void> recordProductView(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long productId) {
        Long memberId = userPrincipal != null ? userPrincipal.getId() : null;
        if (memberId != null) {
            recommendationService.recordProductView(memberId, productId);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> generateRecommendations() {
        recommendationService.generateRecommendations();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/member")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<RecommendationDTO>> getMemberRecommendations(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(recommendationService.getMemberRecommendations(userPrincipal.getId(), pageable));
    }

    @DeleteMapping("/member")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> clearMemberRecommendations(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        recommendationService.clearMemberRecommendations(userPrincipal.getId());
        return ResponseEntity.ok().build();
    }
}