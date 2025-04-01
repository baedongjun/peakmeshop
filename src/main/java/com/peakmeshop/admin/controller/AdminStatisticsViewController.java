package com.peakmeshop.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 관리자 통계 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin")
public class AdminStatisticsViewController {

    /**
     * 통계 메인 페이지
     */
    @GetMapping("/statistics")
    public String statistics() {
        return "admin/statistics";
    }

    /**
     * 매출 통계 페이지
     */
    @GetMapping("/statistics/sales")
    public String salesStatistics() {
        return "admin/statistics-sales";
    }

    /**
     * 상품 통계 페이지
     */
    @GetMapping("/statistics/products")
    public String productStatistics() {
        return "admin/statistics-products";
    }

    /**
     * 보고서 페이지
     */
    @GetMapping("/reports")
    public String reports() {
        return "admin/reports";
    }
}

