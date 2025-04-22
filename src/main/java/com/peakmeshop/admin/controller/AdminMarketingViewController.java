package com.peakmeshop.admin.controller;

import com.peakmeshop.api.dto.CouponDTO;
import com.peakmeshop.api.dto.PromotionDTO;
import com.peakmeshop.domain.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

/**
 * 관리자 마케팅 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminMarketingViewController {

    private final CouponService couponService;
    private final PromotionService promotionService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final MarketingService marketingService;

    /**
     * 쿠폰 관리 페이지
     */
    @GetMapping("/coupons")
    public String coupons(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {
        if (status != null) {
            model.addAttribute("status", status);
        }
        
        Page<CouponDTO> coupons = marketingService.getCoupons(status, pageable);
        model.addAttribute("coupons", coupons);
        model.addAttribute("currentPage", coupons.getNumber());
        model.addAttribute("totalPages", coupons.getTotalPages());
        model.addAttribute("totalElements", coupons.getTotalElements());
        model.addAttribute("pageSize", coupons.getSize());
        model.addAttribute("hasPrevious", coupons.hasPrevious());
        model.addAttribute("hasNext", coupons.hasNext());
        model.addAttribute("isFirst", coupons.isFirst());
        model.addAttribute("isLast", coupons.isLast());
        
        return "admin/marketing/coupons";
    }

    /**
     * 쿠폰 등록 페이지
     */
    @GetMapping("/coupons/new")
    public String newCoupon() {
        return "admin/marketing/coupon-form";
    }

    /**
     * 쿠폰 수정 페이지
     */
    @GetMapping("/coupons/{id}/edit")
    public String editCoupon(@PathVariable Long id, Model model) {
        model.addAttribute("coupon", couponService.getCoupon(id)
                .orElseThrow(() -> new UsernameNotFoundException("Coupon id : "+ id)));
        return "admin/marketing/coupon-form";
    }

    /**
     * 쿠폰 상세 페이지
     */
    @GetMapping("/coupons/{id}")
    public String couponDetail(@PathVariable Long id, Model model) {
        model.addAttribute("coupon", couponService.getCoupon(id)
                .orElseThrow(() -> new UsernameNotFoundException("Coupon id: " + id)));
        return "admin/marketing/coupon-detail";
    }

    /**
     * 프로모션 관리 페이지
     */
    @GetMapping("/promotions")
    public String promotions(
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {
        if (isActive != null) {
            model.addAttribute("isActive", isActive);
        }
        
        Page<PromotionDTO> promotions = marketingService.getPromotions(isActive, pageable);
        model.addAttribute("promotions", promotions);
        model.addAttribute("currentPage", promotions.getNumber());
        model.addAttribute("totalPages", promotions.getTotalPages());
        model.addAttribute("totalElements", promotions.getTotalElements());
        model.addAttribute("pageSize", promotions.getSize());
        model.addAttribute("hasPrevious", promotions.hasPrevious());
        model.addAttribute("hasNext", promotions.hasNext());
        model.addAttribute("isFirst", promotions.isFirst());
        model.addAttribute("isLast", promotions.isLast());
        
        return "admin/marketing/promotions";
    }

    /**
     * 프로모션 등록 페이지
     */
    @GetMapping("/promotions/new")
    public String newPromotion(@PageableDefault(size = 10) Pageable pageable, Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("products", productService.getAllProducts(pageable));
        return "admin/marketing/promotion-form";
    }

    /**
     * 프로모션 수정 페이지
     */
    @GetMapping("/promotions/{id}/edit")
    public String editPromotion(@PathVariable Long id, @PageableDefault(size = 10) Pageable pageable, Model model) {
        model.addAttribute("promotion", promotionService.getPromotion(id)
                .orElseThrow(() -> new UsernameNotFoundException("Promotion id : " + id)));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("products", productService.getAllProducts(pageable));
        return "admin/marketing/promotion-form";
    }

    /**
     * 프로모션 상세 페이지
     */
    @GetMapping("/promotions/{id}")
    public String promotionDetail(@PathVariable Long id, Model model) {
        var promotion = promotionService.getPromotion(id)
                .orElseThrow(() -> new UsernameNotFoundException("Promotion id : "+id));
        model.addAttribute("promotion", promotion);

        if (promotion.getTarget().equals("CATEGORY")) {
            model.addAttribute("categories", categoryService.getCategoryById(promotion.getCategoryId()));
        } else if (promotion.getTarget().equals("PRODUCT")) {
            model.addAttribute("products", productService.getProductById(promotion.getProductId()));
        }

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
            Model model) {
        // TODO: 마케팅 통계 데이터 조회 및 모델 설정
        model.addAttribute("period", period);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "admin/marketing/statistics";
    }
}

