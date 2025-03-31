package com.peakmeshop.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ShopViewController {

    @GetMapping("/")
    public String shopIndex() {
        return "shop/index";
    }

    @GetMapping("/products")
    public String productList() {
        return "shop/product-list";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id) {
        // 실제 구현에서는 여기서 상품 정보를 모델에 추가해야 합니다
        return "shop/product-detail";
    }

    @GetMapping("/category/{categoryName}")
    public String categoryProducts(@PathVariable String categoryName) {
        // 실제 구현에서는 여기서 카테고리별 상품 정보를 모델에 추가해야 합니다
        return "shop/product-list";
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam String query) {
        // 실제 구현에서는 여기서 검색 결과를 모델에 추가해야 합니다
        return "shop/product-list";
    }

    @GetMapping("/cart")
    public String cart() {
        return "shop/cart";
    }

    @GetMapping("/checkout")
    public String checkout() {
        return "shop/checkout";
    }

    @GetMapping("/mypage")
    public String myPage() {
        return "shop/mypage";
    }

    @GetMapping("/mypage/orders")
    public String myOrders() {
        return "shop/my-orders";
    }

    @GetMapping("/mypage/orders/{id}")
    public String myOrderDetail(@PathVariable Long id) {
        // 실제 구현에서는 여기서 주문 정보를 모델에 추가해야 합니다
        return "shop/order-detail";
    }
}
