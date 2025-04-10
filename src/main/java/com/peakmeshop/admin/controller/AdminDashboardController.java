package com.peakmeshop.admin.controller;

import com.peakmeshop.api.dto.*;
import com.peakmeshop.domain.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ProductService productService;
    private final CouponService couponService;
    private final PromotionService promotionService;

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model
    ) {
        // 주문 통계
        OrderSummaryDTO orderSummary = orderService.getOrderSummary(period, startDate, endDate);
        model.addAttribute("orderSummary", orderSummary);

        // 회원 통계
        MemberSummaryDTO memberSummary = memberService.getMemberSummary(period, startDate, endDate);
        model.addAttribute("memberSummary", memberSummary);

        // 상품 통계
        ProductSummaryDTO productSummary = productService.getProductSummary(period, startDate, endDate);
        model.addAttribute("productSummary", productSummary);

        // 쿠폰 통계
        CouponSummaryDTO couponSummary = couponService.getCouponSummary();
        model.addAttribute("couponSummary", couponSummary);

        // 프로모션 통계
        PromotionSummaryDTO promotionSummary = promotionService.getPromotionSummary();
        model.addAttribute("promotionSummary", promotionSummary);

        // 기간 정보
        model.addAttribute("period", period);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "admin/dashboard";
    }
} 