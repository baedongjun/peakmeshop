package com.peakmeshop.admin.controller;

import com.peakmeshop.api.dto.PopupDTO;
import com.peakmeshop.domain.service.PopupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 팝업 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin/popups")
@RequiredArgsConstructor
public class AdminPopupViewController {

    private final PopupService popupService;

    /**
     * 팝업 목록 페이지
     */
    @GetMapping
    public String popups(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {
        Page<PopupDTO> popups = popupService.getAllPopups(pageable);
        
        model.addAttribute("popups", popups);
        model.addAttribute("status", status);
        return "admin/popups/popups";
    }

    /**
     * 팝업 등록 페이지
     */
    @GetMapping("/new")
    public String createPopup(Model model) {
        model.addAttribute("popup", new PopupDTO());
        return "admin/popups/popup-form";
    }

    /**
     * 팝업 수정 페이지
     */
    @GetMapping("/{id}/edit")
    public String editPopup(@PathVariable Long id, Model model) {
        PopupDTO popup = popupService.getPopupById(id);
        model.addAttribute("popup", popup);
        return "admin/popups/popup-form";
    }

    /**
     * 팝업 상세 페이지
     */
    @GetMapping("/{id}")
    public String popupDetail(@PathVariable Long id, Model model) {
        PopupDTO popup = popupService.getPopupById(id);
        model.addAttribute("popup", popup);
        return "admin/popups/detail";
    }

    /**
     * 팝업 미리보기
     */
    @GetMapping("/{id}/preview")
    public String previewPopup(@PathVariable Long id, Model model) {
        PopupDTO popup = popupService.getPopupById(id);
        model.addAttribute("popup", popup);
        return "admin/popups/preview";
    }
} 