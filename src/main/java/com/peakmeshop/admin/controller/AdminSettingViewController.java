package com.peakmeshop.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 관리자 설정 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin")
public class AdminSettingViewController {

    /**
     * 설정 관리 페이지
     */
    @GetMapping("/settings")
    public String settings(@RequestParam(required = false, defaultValue = "basic") String tab, Model model) {
        model.addAttribute("activeTab", tab);
        return "admin/settings/settings";
    }

    /**
     * 기본 설정 페이지
     */
    @GetMapping("/settings/basic")
    public String basicSettings(Model model) {
        return "admin/settings/basic";
    }

    /**
     * 결제 설정 페이지
     */
    @GetMapping("/settings/payment")
    public String paymentSettings(Model model) {
        return "admin/settings/payment";
    }

    /**
     * 배송 설정 페이지
     */
    @GetMapping("/settings/delivery")
    public String deliverySettings(Model model) {
        return "admin/settings/delivery";
    }

    /**
     * 포인트/적립금 설정 페이지
     */
    @GetMapping("/settings/point")
    public String pointSettings(Model model) {
        return "admin/settings/point";
    }

    /**
     * SMS 설정 페이지
     */
    @GetMapping("/settings/sms")
    public String smsSettings(Model model) {
        return "admin/settings/sms";
    }
} 