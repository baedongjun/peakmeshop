package com.peakmeshop.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peakmeshop.dto.ProductComparisonDTO;
import com.peakmeshop.dto.ProductDTO;
import com.peakmeshop.security.oauth2.user.UserPrincipal;
import com.peakmeshop.service.ProductComparisonService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/product-comparison")
@RequiredArgsConstructor
public class ProductComparisonController {

    private final ProductComparisonService productComparisonService;

    @PostMapping("/compare")
    public ResponseEntity<Map<String, Object>> compareProducts(@RequestBody List<Long> productIds) {
        return ResponseEntity.ok(productComparisonService.compareProducts(productIds));
    }

    @GetMapping
    public ResponseEntity<ProductComparisonDTO> getComparisonList(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(productComparisonService.getComparisonList(userPrincipal.getId()));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ProductDTO>> getRecentlyComparedProducts(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(productComparisonService.getRecentlyComparedProducts(userPrincipal.getId(), limit));
    }

    @PostMapping
    public ResponseEntity<Void> addProductsToComparison(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody List<Long> productIds) {
        productComparisonService.addProductsToComparison(userPrincipal.getId(), productIds);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{productId}")
    public ResponseEntity<Void> addProductToComparison(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long productId) {
        productComparisonService.addProductToComparison(userPrincipal.getId(), productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeProductFromComparison(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long productId) {
        productComparisonService.removeProductFromComparison(userPrincipal.getId(), productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/recent")
    public ResponseEntity<Void> clearRecentlyCompared(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        productComparisonService.clearRecentlyCompared(userPrincipal.getId());
        return ResponseEntity.ok().build();
    }
}