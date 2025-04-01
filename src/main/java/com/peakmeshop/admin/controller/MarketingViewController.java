package com.peakmeshop.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 관리자 마케팅 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin")
public class MarketingViewController {

    /**
     * 쿠폰 관리 페이지
     */
    @GetMapping("/coupons")
    public String coupons() {
        return "admin/coupons";
    }

    /**
     * 쿠폰 등록 페이지
     */
    @GetMapping("/coupons/new")
    public String newCoupon() {
        return "admin/coupon-form";
    }

    /**
     * 쿠폰 수정 페이지
     */
    @GetMapping("/coupons/edit/{id}")
    public String editCoupon(@PathVariable Long id, Model model) {
        // 실제 구현에서는 id를 사용하여 쿠폰 정보를 조회하고 모델에 추가
        return "admin/coupon-form";
    }

    /**
     * 배너 관리 페이지
     */
    @GetMapping("/banners")
    public String banners() {
        return "admin/banners";
    }

    /**
     * 배너 등록 페이지
     */
    @GetMapping("/banners/new")
    public String newBanner() {
        return "admin/banner-form";
    }

    /**
     * 배너 수정 페이지
     */
    @GetMapping("/banners/edit/{id}")
    public String editBanner(@PathVariable Long id, Model model) {
        return "admin/banner-form";
    }

    /**
     * 프로모션 관리 페이지
     */
    @GetMapping("/promotions")
    public String promotions() {
        return "admin/promotions";
    }

    /**
     * 프로모션 등록 페이지
     */
    @GetMapping("/promotions/new")
    public String newPromotion() {
        return "admin/promotion-form";
    }

    /**
     * 프로모션 수정 페이지
     */
    @GetMapping("/promotions/edit/{id}")
    public String editPromotion(@PathVariable Long id, Model model) {
        return "admin/promotion-form";
    }
}

