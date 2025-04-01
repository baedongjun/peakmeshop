package com.peakmeshop.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 관리자 공급업체 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin")
public class SupplierViewController {

    /**
     * 공급업체 관리 페이지
     */
    @GetMapping("/suppliers")
    public String suppliers() {
        return "admin/suppliers";
    }

    /**
     * 공급업체 등록 페이지
     */
    @GetMapping("/suppliers/new")
    public String newSupplier() {
        return "admin/supplier-form";
    }

    /**
     * 공급업체 수정 페이지
     */
    @GetMapping("/suppliers/edit/{id}")
    public String editSupplier(@PathVariable Long id, Model model) {
        // 실제 구현에서는 id를 사용하여 공급업체 정보를 조회하고 모델에 추가
        // model.addAttribute("supplier", supplierService.getSupplierById(id));
        return "admin/supplier-form";
    }
}

