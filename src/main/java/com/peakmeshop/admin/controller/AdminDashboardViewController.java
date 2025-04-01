package com.peakmeshop.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 관리자 대시보드 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin")
public class AdminDashboardViewController {

    /**
     * 관리자 대시보드 페이지
     */
    @GetMapping({"/", "/dashboard"})
    public String dashboard() {
        return "admin/dashboard";
    }

    /**
     * 관리자 프로필 페이지
     */
    @GetMapping("/profile")
    public String profile() {
        return "admin/profile";
    }

    /**
     * 관리자 설정 페이지
     */
    @GetMapping("/settings")
    public String settings() {
        return "admin/settings";
    }
}

