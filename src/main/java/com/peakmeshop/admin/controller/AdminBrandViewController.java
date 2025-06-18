package com.peakmeshop.admin.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.peakmeshop.domain.service.BrandService;
import lombok.RequiredArgsConstructor;

/**
 * 관리자 브랜드 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin/brands")
@RequiredArgsConstructor
public class AdminBrandViewController {

    private final BrandService brandService;

    /**
     * 브랜드 관리 페이지
     */
    @GetMapping
    public String brands(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {
        if (status != null) {
            model.addAttribute("status", status);
        }

        model.addAttribute("summary", brandService.getBrandSummary());
        model.addAttribute("brands", brandService.getAllBrands(pageable));
        
        return "admin/brands/brands";
    }

    /**
     * 브랜드 등록 페이지
     */
    @GetMapping("/new")
    public String createBrand() {
        return "admin/brands/brand-form";
    }

    /**
     * 브랜드 수정 페이지
     */
    @GetMapping("/{id}/edit")
    public String editBrand(@PathVariable Long id, Model model) {
        model.addAttribute("brandId", id);
        model.addAttribute("brand", brandService.getBrandById(id));
        return "admin/brands/brand-form";
    }

    /**
     * 브랜드 상세 페이지
     */
    @GetMapping("/{id}")
    public String brandDetail(@PathVariable Long id, Model model) {
        model.addAttribute("brandId", id);
        model.addAttribute("brand", brandService.getBrandById(id));
        model.addAttribute("summary", brandService.getBrandSummary(id));
        return "admin/brands/brand-detail";
    }

    /**
     * 브랜드 통계 페이지
     */
    @GetMapping("/statistics")
    public String brandStatistics(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
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

        model.addAttribute("statistics", brandService.getBrandStatistics(period, startDate, endDate));
        return "admin/brands/statistics";
    }
}

