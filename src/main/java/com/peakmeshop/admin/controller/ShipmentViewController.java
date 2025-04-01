package com.peakmeshop.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 관리자 배송 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin")
public class ShipmentViewController {

    /**
     * 배송 관리 페이지
     */
    @GetMapping("/shipments")
    public String shipments() {
        return "admin/shipments";
    }

    /**
     * 배송 등록 페이지
     */
    @GetMapping("/shipments/new")
    public String newShipment() {
        return "admin/shipment-form";
    }

    /**
     * 배송 상세/수정 페이지
     */
    @GetMapping("/shipments/{id}")
    public String shipmentDetail(@PathVariable Long id, Model model) {
        // 실제 구현에서는 id를 사용하여 배송 정보를 조회하고 모델에 추가
        // model.addAttribute("shipment", shipmentService.getShipmentById(id));
        return "admin/shipment-form";
    }
}

