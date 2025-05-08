package com.peakmeshop.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        return "shop/product/product-list";
    }

    /**
     * 상품 상세 페이지
     */
    @GetMapping("/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        // 실제 구현에서는 여기서 상품 정보를 모델에 추가해야 합니다
        model.addAttribute("productId", id);
        return "shop/product/product-detail";
    }

    /**
     * 상품 리뷰 작성 페이지
     */
    @GetMapping("/{productId}/review")
    public String writeReview(@PathVariable Long productId, Model model) {
        model.addAttribute("productId", productId);
        return "shop/product/review-form";
    }

    /**
     * 상품 리뷰 목록
     */
    @GetMapping("/{productId}/reviews")
    public String productReviews(@PathVariable Long productId, Model model) {
        model.addAttribute("productId", productId);
        return "shop/product/product-reviews";
    }

    /**
     * 카테고리별 상품 목록
     */
    @GetMapping("/category/{categoryName}")
    public String categoryProducts(@PathVariable String categoryName, Model model) {
        // 실제 구현에서는 여기서 카테고리별 상품 정보를 모델에 추가해야 합니다
        model.addAttribute("categoryName", categoryName);
        return "shop/product/product-list";
    }

    /**
     * 상품 검색 결과
     */
    @GetMapping("/search")
    public String searchProducts(@RequestParam String query, Model model) {
        // 실제 구현에서는 여기서 검색 결과를 모델에 추가해야 합니다
        model.addAttribute("searchQuery", query);
        return "shop/product/product-list";
    }

    /**
     * 신상품 목록
     */
    @GetMapping("/new-arrivals")
    public String newArrivals(Model model) {
        model.addAttribute("listType", "new");
        return "shop/product/product-list";
    }

    /**
     * 베스트셀러 목록
     */
    @GetMapping("/best-sellers")
    public String bestSellers(Model model) {
        model.addAttribute("listType", "best");
        return "shop/product/product-list";
    }

    /**
     * 세일 상품 목록
     */
    @GetMapping("/sale")
    public String saleProducts(Model model) {
        model.addAttribute("listType", "sale");
        return "shop/product/product-list";
    }

    /**
     * 브랜드별 상품 목록
     */
    @GetMapping("/brand/{brandName}")
    public String brandProducts(@PathVariable String brandName, Model model) {
        model.addAttribute("brandName", brandName);
        return "shop/product/product-list";
    }
}

