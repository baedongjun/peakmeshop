package com.peakmeshop.admin.controller;

import com.peakmeshop.api.dto.SupplierDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.peakmeshop.domain.service.SupplierService;
import lombok.RequiredArgsConstructor;

/**
 * 관리자 공급사 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminSupplierViewController {

    private final SupplierService supplierService;

    /**
     * 공급사 관리 페이지
     */
    @GetMapping("/suppliers")
    public String suppliers(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {
        if (status != null) {
            model.addAttribute("status", status);
        }

        model.addAttribute("summary", supplierService.getSupplierSummary());
        model.addAttribute("suppliers", supplierService.getAllSuppliers(pageable));
        
        return "admin/suppliers/suppliers";
    }

    /**
     * 공급사 등록 페이지
     */
    @GetMapping("/suppliers/new")
    public String createSupplier() {
        return "admin/suppliers/supplier-form";
    }

    /**
     * 공급사 수정 페이지
     */
    @GetMapping("/suppliers/{id}/edit")
    public String editSupplier(@PathVariable Long id, Model model) {
        model.addAttribute("supplierId", id);
        model.addAttribute("supplier", supplierService.getSupplierById(id));
        return "admin/suppliers/supplier-form";
    }

    /**
     * 공급사 상세 페이지
     */
    @GetMapping("/suppliers/{id}")
    public String supplierDetail(@PathVariable Long id, @PageableDefault(size = 20) Pageable pageable, Model model) {
        SupplierDTO supplier = supplierService.getSupplierById(id)
                .orElseThrow(() -> new IllegalArgumentException("공급사를 찾을 수 없습니다: " + id));
        model.addAttribute("supplierId", id);
        model.addAttribute("supplier", supplier);
        model.addAttribute("summary", supplierService.getSupplierSummary(id));
        model.addAttribute("products", supplierService.getSupplierProducts(id, pageable));
        return "admin/suppliers/supplier-detail";
    }

    /**
     * 공급사 정산 관리 페이지
     */
    @GetMapping("/suppliers/settlements")
    public String settlements(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {
        if (status != null) {
            model.addAttribute("status", status);
        }

        model.addAttribute("settlements", supplierService.getSettlements(pageable));
        return "admin/suppliers/settlements";
    }

    /**
     * 공급사 정산 상세 페이지
     */
    @GetMapping("/suppliers/settlements/{id}")
    public String settlementDetail(@PathVariable Long id, Model model) {
        model.addAttribute("settlementId", id);
        return "admin/suppliers/settlement-detail";
    }

    /**
     * 공급사 통계 페이지
     */
    @GetMapping("/suppliers/statistics")
    public String supplierStatistics(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {
        if (period != null) {
            model.addAttribute("period", period);
        }
        if (startDate != null) {
            model.addAttribute("startDate", startDate);
        }
        if (endDate != null) {
            model.addAttribute("endDate", endDate);
        }
        return "admin/suppliers/statistics";
    }

    /**
     * 공급사 계약 관리 페이지
     */
    @GetMapping("/suppliers/contracts")
    public String contracts(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {
        if (status != null) {
            model.addAttribute("status", status);
        }

        model.addAttribute("contracts", supplierService.getContracts(pageable));
        return "admin/suppliers/contracts";
    }

    /**
     * 공급사 계약 상세 페이지
     */
    @GetMapping("/suppliers/contracts/{id}")
    public String contractDetail(@PathVariable Long id, Model model) {
        model.addAttribute("contractId", id);
        model.addAttribute("contract", supplierService.getContractById(id));
        return "admin/suppliers/contract-detail";
    }
}

