package com.peakmeshop.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 상품 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/products")
public class ProductViewController {

    /**
     * 상품 목록
     */
    @GetMapping
    public String productList() {
        return "shop/product-list";
    }

    /**
     * 상품 상세 페이지
     */
    @GetMapping("/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        // 실제 구현에서는 여기서 상품 정보를 모델에 추가해야 합니다
        model.addAttribute("productId", id);
        return "shop/product-detail";
    }

    /**
     * 상품 리뷰 작성 페이지
     */
    @GetMapping("/{productId}/review")
    public String writeReview(@PathVariable Long productId, Model model) {
        model.addAttribute("productId", productId);
        return "shop/review-form";
    }

    /**
     * 상품 리뷰 목록
     */
    @GetMapping("/{productId}/reviews")
    public String productReviews(@PathVariable Long productId, Model model) {
        model.addAttribute("productId", productId);
        return "shop/product-reviews";
    }
}

