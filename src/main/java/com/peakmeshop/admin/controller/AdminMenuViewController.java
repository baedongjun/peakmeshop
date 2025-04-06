package com.peakmeshop.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 관리자 메뉴 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin")
public class AdminMenuViewController {

    /**
     * 메뉴 관리 페이지
     */
    @GetMapping("/menus")
    public String menus(@RequestParam(required = false, defaultValue = "HEADER") String type, Model model) {
        model.addAttribute("menuType", type);
        return "admin/menus/menus";
    }
} 