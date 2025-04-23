package com.peakmeshop.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 주문, 결제 관련 뷰 컨트롤러
 */
@Controller
public class OrderViewController {

    /**
     * 장바구니
     */
    @GetMapping("/cart")
    public String cart() {
        return "shop/cart";
    }

    /**
     * 장바구니 견적서
     */
    @GetMapping("/cart/estimate")
    public String cartEstimate() {
        return "shop/cart-estimate";
    }

    /**
     * 주문/결제 페이지
     */
    @GetMapping("/checkout")
    public String checkout() {
        return "shop/checkout";
    }

    /**
     * 주문 완료 페이지
     */
    @GetMapping("/order-complete")
    public String orderComplete(@RequestParam(required = false) String orderNumber, Model model) {
        model.addAttribute("orderNumber", orderNumber);
        return "shop/order-complete";
    }

    /**
     * 주문 취소 페이지
     */
    @GetMapping("/mypage/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "shop/order-cancel";
    }

    /**
     * 환불 신청 페이지
     */
    @GetMapping("/mypage/orders/{orderId}/refund")
    public String refundOrder(@PathVariable Long orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "shop/order-refund";
    }

    /**
     * 반품 신청 페이지
     */
    @GetMapping("/mypage/orders/{orderId}/return")
    public String returnOrder(@PathVariable Long orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "shop/order-return";
    }

    /**
     * 교환 신청 페이지
     */
    @GetMapping("/mypage/orders/{orderId}/exchange")
    public String exchangeOrder(@PathVariable Long orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "shop/order-exchange";
    }
}

