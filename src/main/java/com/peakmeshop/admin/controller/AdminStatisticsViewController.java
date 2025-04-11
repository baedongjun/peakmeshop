package com.peakmeshop.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 관리자 통계 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin/statistics")
public class AdminStatisticsViewController {

    /**
     * 통계 메인 페이지
     */
    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {
        model.addAttribute("type", type);
        model.addAttribute("period", period);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "admin/statistics/dashboard";
    }

    /**
     * 통계 메인 페이지
     */
    @GetMapping("/statistics")
    public String statistics(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {
        model.addAttribute("type", type);
        model.addAttribute("period", period);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "admin/statistics/statistics";
    }

    /**
     * 매출 통계 페이지
     */
    @GetMapping("/statistics/sales")
    public String salesStatistics(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {
        model.addAttribute("period", period);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "admin/statistics/sales";
    }

    /**
     * 상품 통계 페이지
     */
    @GetMapping("/statistics/products")
    public String productStatistics(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String category,
            Model model) {
        model.addAttribute("period", period);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("category", category);
        return "admin/statistics/products";
    }

    /**
     * 회원 통계 페이지
     */
    @GetMapping("/statistics/members")
    public String memberStatistics(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {
        model.addAttribute("period", period);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "admin/statistics/members";
    }

    /**
     * 방문자 통계 페이지
     */
    @GetMapping("/statistics/visitors")
    public String visitorStatistics(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {
        model.addAttribute("period", period);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "admin/statistics/visitors";
    }

    /**
     * 보고서 페이지
     */
    @GetMapping("/reports")
    public String reports(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String period,
            Model model) {
        model.addAttribute("type", type);
        model.addAttribute("period", period);
        return "admin/statistics/reports";
    }

    /**
     * 보고서 상세 페이지
     */
    @GetMapping("/reports/detail")
    public String reportDetail(
            @RequestParam String type,
            @RequestParam String period,
            @RequestParam String date,
            Model model) {
        model.addAttribute("type", type);
        model.addAttribute("period", period);
        model.addAttribute("date", date);
        return "admin/statistics/report-detail";
    }
}

