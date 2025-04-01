package com.peakmeshop.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 관리자 주문 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin")
public class OrderViewController {

    /**
     * 주문 관리 페이지
     */
    @GetMapping("/orders")
    public String orders() {
        return "admin/orders";
    }

    /**
     * 주문 상세 페이지
     */
    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        // 실제 구현에서는 id를 사용하여 주문 정보를 조회하고 모델에 추가
        // model.addAttribute("order", orderService.getOrderById(id));
        return "admin/order-detail";
    }

    /**
     * 환불 관리 페이지
     */
    @GetMapping("/refunds")
    public String refunds() {
        return "admin/refunds";
    }

    /**
     * 환불 상세 페이지
     */
    @GetMapping("/refunds/{id}")
    public String refundDetail(@PathVariable Long id, Model model) {
        return "admin/refund-detail";
    }
}

