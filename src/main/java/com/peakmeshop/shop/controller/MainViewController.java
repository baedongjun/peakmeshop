package com.peakmeshop.shop.controller;

import com.peakmeshop.domain.service.BrandService;
import com.peakmeshop.domain.service.CategoryService;
import com.peakmeshop.domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 쇼핑몰 메인 페이지, 카테고리, 검색 등 기본 쇼핑 기능 관련 뷰 컨트롤러
 */
@Controller
@RequiredArgsConstructor
public class MainViewController {
    private final CategoryService categoryService;
    private final ProductService productService;
    private final BrandService brandService;

    /**
     * 쇼핑몰 메인 페이지
     */
    @GetMapping("/")
    public String shopIndex(Model model) {
        model.addAttribute("categories", categoryService.getFeaturedCategories());
        model.addAttribute("newProducts", productService.getNewArrivals(Pageable.ofSize(4)));
        model.addAttribute("bestProducts", productService.getBestSellers(Pageable.ofSize(4)));
        model.addAttribute("discountedProducts", productService.getDiscountedProducts(Pageable.ofSize(4)));
        model.addAttribute("brands", brandService.getFeaturedBrands());
        return "shop/index";
    }
}

