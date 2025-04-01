package com.peakmeshop.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String products() {
        return "admin/products";
    }

    @GetMapping("/products/new")
    public String newProduct() {
        return "admin/product-form";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Long id) {
        // 실제 구현에서는 여기서 상품 정보를 모델에 추가해야 합니다
        return "admin/product-form";
    }

    @GetMapping("/orders")
    public String orders() {
        return "admin/orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id) {
        // 실제 구현에서는 여기서 주문 정보를 모델에 추가해야 합니다
        return "admin/order-detail";
    }

    @GetMapping("/members")
    public String members() {
        return "admin/members";
    }

    @GetMapping("/suppliers")
    public String suppliers() {
        return "admin/suppliers";
    }

    @GetMapping("/shipments")
    public String shipments() {
        return "admin/shipments";
    }
}

