package com.peakmeshop.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 관리자 상품 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin")
public class AdminProductViewController {

    /**
     * 상품 관리 페이지
     */
    @GetMapping("/products")
    public String products() {
        return "admin/products";
    }

    /**
     * 상품 등록 페이지
     */
    @GetMapping("/products/new")
    public String newProduct() {
        return "admin/product-form";
    }

    /**
     * 상품 수정 페이지
     */
    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        // 실제 구현에서는 id를 사용하여 상품 정보를 조회하고 모델에 추가
        // model.addAttribute("product", productService.getProductById(id));
        return "admin/product-form";
    }
}

