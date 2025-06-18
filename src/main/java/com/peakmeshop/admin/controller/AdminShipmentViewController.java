package com.peakmeshop.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 관리자 배송 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin/shipments")
public class AdminShipmentViewController {

    /**
     * 배송 관리 페이지
     */
    @GetMapping
    public String shipments(
            @RequestParam(required = false) String status,
            Model model) {
        if (status != null) {
            model.addAttribute("status", status);
        }
        return "admin/shipments/shipments";
    }

    /**
     * 배송 등록 페이지
     */
    @GetMapping("/new")
    public String createShipment() {
        return "admin/shipments/shipments-form";
    }

    /**
     * 배송 상세/수정 페이지
     */
    @GetMapping("/{id}")
    public String shipmentDetail(@PathVariable Long id, Model model) {
        model.addAttribute("shipmentId", id);
        return "admin/shipments/shipments-detail";
    }

    /**
     * 배송 수정 페이지
     */
    @GetMapping("/{id}/edit")
    public String editShipment(@PathVariable Long id, Model model) {
        model.addAttribute("shipmentId", id);
        return "admin/shipments/shipments-form";
    }

    /**
     * 배송비 설정 페이지
     */
    @GetMapping("/fees")
    public String shippingFees(Model model) {
        return "admin/shipments/fees";
    }

    /**
     * 배송 지역 설정 페이지
     */
    @GetMapping("/areas")
    public String shippingAreas(Model model) {
        return "admin/shipments/areas";
    }

    /**
     * 택배사 관리 페이지
     */
    @GetMapping("/carriers")
    public String shippingCarriers(Model model) {
        return "admin/shipments/carriers";
    }

    /**
     * 배송 통계 페이지
     */
    @GetMapping("/statistics")
    public String shipmentStatistics(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {
        model.addAttribute("period", period);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "admin/shipments/statistics";
    }

    /**
     * 반품/교환 관리 페이지
     */
    @GetMapping("/returns")
    public String returns(
            @RequestParam(required = false) String status,
            Model model) {
        if (status != null) {
            model.addAttribute("status", status);
        }
        return "admin/shipments/returns";
    }

    /**
     * 반품/교환 상세 페이지
     */
    @GetMapping("/returns/{id}")
    public String returnDetail(@PathVariable Long id, Model model) {
        model.addAttribute("returnId", id);
        return "admin/shipments/return-detail";
    }
}

