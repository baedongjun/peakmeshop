package com.peakmeshop.admin.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String notices(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {
        if (category != null) {
            model.addAttribute("category", category);
        }
        if (status != null) {
            model.addAttribute("status", status);
        }
        model.addAttribute("pageable", pageable);
        return "admin/contents/notices";
    }

    /**
     * 공지사항 등록 페이지
     */
    @GetMapping("/notices/new")
    public String createNotice() {
        return "admin/contents/notice-form";
    }

    /**
     * 공지사항 수정 페이지
     */
    @GetMapping("/notices/{id}/edit")
    public String editNotice(@PathVariable Long id, Model model) {
        model.addAttribute("noticeId", id);
        return "admin/contents/notice-form";
    }

    /**
     * FAQ 관리 페이지
     */
    @GetMapping("/faqs")
    public String faqs(
            @RequestParam(required = false) String category,
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {
        if (category != null) {
            model.addAttribute("category", category);
        }
        model.addAttribute("pageable", pageable);
        return "admin/contents/faqs";
    }

    /**
     * FAQ 등록 페이지
     */
    @GetMapping("/faqs/new")
    public String createFaq() {
        return "admin/contents/faq-form";
    }

    /**
     * FAQ 수정 페이지
     */
    @GetMapping("/faqs/{id}/edit")
    public String editFaq(@PathVariable Long id, Model model) {
        model.addAttribute("faqId", id);
        return "admin/contents/faq-form";
    }

    /**
     * FAQ 카테고리 관리 페이지
     */
    @GetMapping("/faqs/categories")
    public String faqCategories(Model model) {
        return "admin/contents/faq-categories";
    }

    /**
     * 1:1 문의 관리 페이지
     */
    @GetMapping("/inquiries")
    public String inquiries(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {
        if (status != null) {
            model.addAttribute("status", status);
        }
        model.addAttribute("pageable", pageable);
        return "admin/contents/inquiries";
    }

    /**
     * 1:1 문의 상세 페이지
     */
    @GetMapping("/inquiries/{id}")
    public String inquiryDetail(@PathVariable Long id, Model model) {
        model.addAttribute("inquiryId", id);
        return "admin/contents/inquiry-detail";
    }

    /**
     * 1:1 문의 답변 페이지
     */
    @GetMapping("/inquiries/{id}/reply")
    public String replyInquiry(@PathVariable Long id, Model model) {
        model.addAttribute("inquiryId", id);
        return "admin/contents/inquiry-reply";
    }

    /**
     * 약관 관리 페이지
     */
    @GetMapping("/terms")
    public String terms(Model model) {
        return "admin/contents/terms";
    }

    /**
     * 약관 수정 페이지
     */
    @GetMapping("/terms/{type}/edit")
    public String editTerm(@PathVariable String type, Model model) {
        model.addAttribute("termType", type);
        return "admin/contents/term-form";
    }
}

