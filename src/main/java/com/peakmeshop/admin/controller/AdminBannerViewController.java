package com.peakmeshop.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 관리자 배너 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin")
public class AdminBannerViewController {

    /**
     * 배너 관리 페이지
     */
    @GetMapping("/banners")
    public String banners(Model model) {
        return "admin/banners/banners";
    }
} 