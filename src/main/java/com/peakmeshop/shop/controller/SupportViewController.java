package com.peakmeshop.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 고객 지원, 공지사항, FAQ 등 관련 뷰 컨트롤러
 */
@Controller
public class SupportViewController {

    /**
     * 공지사항 목록
     */
    @GetMapping("/notice")
    public String noticeList() {
        return "shop/support/notice-list";
    }

    /**
     * 공지사항 상세
     */
    @GetMapping("/notice/{id}")
    public String noticeDetail(@PathVariable Long id, Model model) {
        model.addAttribute("noticeId", id);
        return "shop/support/notice-detail";
    }

    /**
     * 자주 묻는 질문
     */
    @GetMapping("/faq")
    public String faq() {
        return "shop/support/faq";
    }

    /**
     * 1:1 문의 (비로그인 상태)
     */
    @GetMapping("/inquiry")
    public String inquiry() {
        return "shop/support/inquiry";
    }

    /**
     * 이용약관
     */
    @GetMapping("/terms")
    public String terms() {
        return "shop/support/terms";
    }

    /**
     * 개인정보처리방침
     */
    @GetMapping("/privacy")
    public String privacy() {
        return "shop/support/privacy";
    }
}

