package com.peakmeshop.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 관리자 마케팅 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin")
public class AdminMarketingViewController {

    /**
     * 쿠폰 관리 페이지
     */
    @GetMapping("/coupons")
    public String coupons(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            Model model) {
        if (type != null) {
            model.addAttribute("type", type);
        }
        if (status != null) {
            model.addAttribute("status", status);
        }
        return "admin/marketing/coupons";
    }

    /**
     * 쿠폰 등록 페이지
     */
    @GetMapping("/coupons/new")
    public String createCoupon() {
        return "admin/marketing/coupon-form";
    }

    /**
     * 쿠폰 수정 페이지
     */
    @GetMapping("/coupons/{id}/edit")
    public String editCoupon(@PathVariable Long id, Model model) {
        model.addAttribute("couponId", id);
        return "admin/marketing/coupon-form";
    }

    /**
     * 쿠폰 상세 페이지
     */
    @GetMapping("/coupons/{id}")
    public String couponDetail(@PathVariable Long id, Model model) {
        model.addAttribute("couponId", id);
        return "admin/marketing/coupon-detail";
    }

    /**
     * 프로모션 관리 페이지
     */
    @GetMapping("/promotions")
    public String promotions(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            Model model) {
        if (type != null) {
            model.addAttribute("type", type);
        }
        if (status != null) {
            model.addAttribute("status", status);
        }
        return "admin/marketing/promotions";
    }

    /**
     * 프로모션 등록 페이지
     */
    @GetMapping("/promotions/new")
    public String createPromotion() {
        return "admin/marketing/promotion-form";
    }

    /**
     * 프로모션 수정 페이지
     */
    @GetMapping("/promotions/{id}/edit")
    public String editPromotion(@PathVariable Long id, Model model) {
        model.addAttribute("promotionId", id);
        return "admin/marketing/promotion-form";
    }

    /**
     * 프로모션 상세 페이지
     */
    @GetMapping("/promotions/{id}")
    public String promotionDetail(@PathVariable Long id, Model model) {
        model.addAttribute("promotionId", id);
        return "admin/marketing/promotion-detail";
    }

    /**
     * 마케팅 통계 페이지
     */
    @GetMapping("/marketing/statistics")
    public String marketingStatistics(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String type,
            Model model) {
        if (period != null) {
            model.addAttribute("period", period);
        }
        if (startDate != null) {
            model.addAttribute("startDate", startDate);
        }
        if (endDate != null) {
            model.addAttribute("endDate", endDate);
        }
        if (type != null) {
            model.addAttribute("type", type);
        }
        return "admin/marketing/statistics";
    }
}

