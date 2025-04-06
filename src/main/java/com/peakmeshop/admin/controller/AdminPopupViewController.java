package com.peakmeshop.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 관리자 팝업 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin")
public class AdminPopupViewController {

    /**
     * 팝업 관리 페이지
     */
    @GetMapping("/popups")
    public String popups(Model model) {
        return "admin/popups/popups";
    }

    /**
     * 팝업 등록 페이지
     */
    @GetMapping("/popups/new")
    public String createPopupForm(Model model) {
        return "admin/popups/form";
    }

    /**
     * 팝업 수정 페이지
     */
    @GetMapping("/popups/{id}/edit")
    public String editPopupForm(@PathVariable Long id, Model model) {
        model.addAttribute("popupId", id);
        return "admin/popups/form";
    }

    /**
     * 팝업 미리보기 페이지
     */
    @GetMapping("/popups/{id}/preview")
    public String previewPopup(@PathVariable Long id, Model model) {
        model.addAttribute("popupId", id);
        return "admin/popups/preview";
    }
} 