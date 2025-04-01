package com.peakmeshop.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 관리자 콘텐츠 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin")
public class AdminContentViewController {

    /**
     * 공지사항 관리 페이지
     */
    @GetMapping("/notices")
    public String notices() {
        return "admin/notices";
    }

    /**
     * 공지사항 등록 페이지
     */
    @GetMapping("/notices/new")
    public String newNotice() {
        return "admin/notice-form";
    }

    /**
     * 공지사항 수정 페이지
     */
    @GetMapping("/notices/edit/{id}")
    public String editNotice(@PathVariable Long id, Model model) {
        return "admin/notice-form";
    }

    /**
     * FAQ 관리 페이지
     */
    @GetMapping("/faqs")
    public String faqs() {
        return "admin/faqs";
    }

    /**
     * 1:1 문의 관리 페이지
     */
    @GetMapping("/inquiries")
    public String inquiries() {
        return "admin/inquiries";
    }

    /**
     * 1:1 문의 상세 페이지
     */
    @GetMapping("/inquiries/{id}")
    public String inquiryDetail(@PathVariable Long id, Model model) {
        return "admin/inquiry-detail";
    }
}

