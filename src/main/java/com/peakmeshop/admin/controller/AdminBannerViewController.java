package com.peakmeshop.admin.controller;

import com.peakmeshop.api.dto.BannerDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.peakmeshop.domain.service.BannerService;
import lombok.RequiredArgsConstructor;

/**
 * 관리자 배너 관리 관련 뷰 컨트롤러
 */ 
@Controller
@RequestMapping("/admin/banners")
@RequiredArgsConstructor
public class AdminBannerViewController {

    private final BannerService bannerService;

    /**
     * 배너 관리 페이지
     */
    @GetMapping
    public String banners(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {
        if (status != null) {
            model.addAttribute("status", status);
        }
        
        Page<BannerDTO> banners = bannerService.getBanners(pageable);
        model.addAttribute("banners", banners);
        model.addAttribute("currentPage", banners.getNumber());
        model.addAttribute("totalPages", banners.getTotalPages());
        model.addAttribute("totalElements", banners.getTotalElements());
        model.addAttribute("pageSize", banners.getSize());
        model.addAttribute("hasPrevious", banners.hasPrevious());
        model.addAttribute("hasNext", banners.hasNext());
        model.addAttribute("isFirst", banners.isFirst());
        model.addAttribute("isLast", banners.isLast());
        
        return "admin/banners/banners";
    }
} 