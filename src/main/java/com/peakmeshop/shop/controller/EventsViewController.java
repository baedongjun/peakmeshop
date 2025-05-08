package com.peakmeshop.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *  이벤트 및 프로모션 관련 뷰 컨트롤러
 */
@Controller
public class EventsViewController {

    /**
     * 이벤트 목록
     */
    @GetMapping("/events")
    public String eventList() {
        return "shop/event/event-list";
    }

    /**
     * 이벤트 상세
     */
    @GetMapping("/events/{id}")
    public String eventDetail(@PathVariable Long id, Model model) {
        model.addAttribute("eventId", id);
        return "shop/event/event-detail";
    }

    /**
     * 프로모션 목록
     */
    @GetMapping("/promotions")
    public String promotionList() {
        return "shop/event/promotion-list";
    }
}

