package com.peakmeshop.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 쇼핑몰 메인 페이지, 카테고리, 검색 등 기본 쇼핑 기능 관련 뷰 컨트롤러
 */
@Controller
public class MainViewController {

    /**
     * 쇼핑몰 메인 페이지
     */
    @GetMapping("/")
    public String shopIndex() {
        return "shop/index";
    }

    /**
     * 카테고리별 상품 목록
     */
    @GetMapping("/category/{categoryName}")
    public String categoryProducts(@PathVariable String categoryName, Model model) {
        // 실제 구현에서는 여기서 카테고리별 상품 정보를 모델에 추가해야 합니다
        model.addAttribute("categoryName", categoryName);
        return "shop/product-list";
    }

    /**
     * 상품 검색 결과
     */
    @GetMapping("/search")
    public String searchProducts(@RequestParam String query, Model model) {
        // 실제 구현에서는 여기서 검색 결과를 모델에 추가해야 합니다
        model.addAttribute("searchQuery", query);
        return "shop/product-list";
    }

    /**
     * 신상품 목록
     */
    @GetMapping("/new-arrivals")
    public String newArrivals(Model model) {
        model.addAttribute("listType", "new");
        return "shop/product-list";
    }

    /**
     * 베스트셀러 목록
     */
    @GetMapping("/best-sellers")
    public String bestSellers(Model model) {
        model.addAttribute("listType", "best");
        return "shop/product-list";
    }

    /**
     * 세일 상품 목록
     */
    @GetMapping("/sale")
    public String saleProducts(Model model) {
        model.addAttribute("listType", "sale");
        return "shop/product-list";
    }

    /**
     * 브랜드별 상품 목록
     */
    @GetMapping("/brand/{brandName}")
    public String brandProducts(@PathVariable String brandName, Model model) {
        model.addAttribute("brandName", brandName);
        return "shop/product-list";
    }

    /**
     * 상품 비교 페이지
     */
    @GetMapping("/compare")
    public String compareProducts() {
        return "shop/product-compare";
    }

    /**
     * 매장 위치 정보
     */
    @GetMapping("/stores")
    public String storeLocations() {
        return "shop/store-locations";
    }

    /**
     * 회사 소개
     */
    @GetMapping("/about")
    public String aboutUs() {
        return "shop/about";
    }
}

