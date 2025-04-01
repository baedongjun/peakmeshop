package com.peakmeshop.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
        return "shop/notice-list";
    }

    /**
     * 공지사항 상세
     */
    @GetMapping("/notice/{id}")
    public String noticeDetail(@PathVariable Long id, Model model) {
        model.addAttribute("noticeId", id);
        return "shop/notice-detail";
    }

    /**
     * 자주 묻는 질문
     */
    @GetMapping("/faq")
    public String faq() {
        return "shop/faq";
    }

    /**
     * 1:1 문의 (비로그인 상태)
     */
    @GetMapping("/inquiry")
    public String inquiry() {
        return "shop/inquiry";
    }

    /**
     * 이용약관
     */
    @GetMapping("/terms")
    public String terms() {
        return "shop/terms";
    }

    /**
     * 개인정보처리방침
     */
    @GetMapping("/privacy")
    public String privacy() {
        return "shop/privacy";
    }

    /**
     * 이벤트 목록
     */
    @GetMapping("/events")
    public String eventList() {
        return "shop/event-list";
    }

    /**
     * 이벤트 상세
     */
    @GetMapping("/events/{id}")
    public String eventDetail(@PathVariable Long id, Model model) {
        model.addAttribute("eventId", id);
        return "shop/event-detail";
    }

    /**
     * 프로모션 목록
     */
    @GetMapping("/promotions")
    public String promotionList() {
        return "shop/promotion-list";
    }
}

