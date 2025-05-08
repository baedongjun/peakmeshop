package com.peakmeshop.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 주문, 결제 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/order")
public class OrderViewController {

    /**
     * 장바구니
     */
    @GetMapping("/cart")
    public String cart() {
        return "shop/order/cart";
    }

    /**
     * 장바구니 견적서
     */
    @GetMapping("/cart/estimate")
    public String cartEstimate() {
        return "shop/order/cart-estimate";
    }

    /**
     * 주문/결제 페이지
     */
    @GetMapping("/checkout")
    public String checkout() {
        return "shop/order/checkout";
    }

    /**
     * 주문 완료 페이지
     */
    @GetMapping("/order-complete")
    public String orderComplete(@RequestParam(required = false) String orderNumber, Model model) {
        model.addAttribute("orderNumber", orderNumber);
        return "shop/order/order-complete";
    }
}

