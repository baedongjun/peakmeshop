package com.peakmeshop.admin.controller;

import com.peakmeshop.domain.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 관리자 주문 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderViewController {

    private final OrderService orderService;

    /**
     * 주문 관리 페이지
     */
    @GetMapping
    public String orders(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {
        if (status != null) {
            model.addAttribute("status", status);
        }

        model.addAttribute("summary", orderService.getOrderSummary());
        model.addAttribute("orders", orderService.getAllOrders(pageable));

        return "admin/orders/orders";
    }

    /**
     * 주문 상세 페이지
     */
    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        model.addAttribute("orderId", id);
        model.addAttribute("order", orderService.getOrderById(id));
        return "admin/orders/detail";
    }

    /**
     * 주문 통계 페이지
     */
    @GetMapping("/statistics")
    public String orderStatistics(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {
        model.addAttribute("period", period);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("statistics", orderService.getOrderStatistics(period, startDate, endDate));
        return "admin/orders/statistics";
    }

    /**
     * 매출 통계 페이지
     */
    @GetMapping("/sales")
    public String salesStatistics(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {
        model.addAttribute("period", period);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("statistics", orderService.getSalesStatistics(period, startDate, endDate));
        return "admin/orders/sales";
    }

    /**
     * 환불 관리 페이지
     */
    @GetMapping("/refunds")
    public String refunds(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {
        if (status != null) {
            model.addAttribute("status", status);
        }
        model.addAttribute("refunds", orderService.getRefunds(pageable));
        return "admin/orders/refunds";
    }

    /**
     * 환불 상세 페이지
     */
    @GetMapping("/refunds/{id}")
    public String refundDetail(@PathVariable Long id, Model model) {
        model.addAttribute("refundId", id);
        model.addAttribute("refund", orderService.getRefundById(id));
        return "admin/orders/refund-detail";
    }

    /**
     * 취소 관리 페이지
     */
    @GetMapping("/cancellations")
    public String cancellations(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {
        if (status != null) {
            model.addAttribute("status", status);
        }
        model.addAttribute("cancellations", orderService.getCancellations(pageable));
        return "admin/orders/cancellations";
    }

    /**
     * 취소 상세 페이지
     */
    @GetMapping("/cancellations/{id}")
    public String cancellationDetail(@PathVariable Long id, Model model) {
        model.addAttribute("cancellationId", id);
        model.addAttribute("cancellation", orderService.getCancellationById(id));
        return "admin/orders/cancellation-detail";
    }
}

