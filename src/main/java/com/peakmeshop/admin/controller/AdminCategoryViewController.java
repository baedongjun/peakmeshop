package com.peakmeshop.admin.controller;

import com.peakmeshop.api.dto.CategoryDTO;
import com.peakmeshop.domain.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관리자 카테고리 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryViewController {

    private final CategoryService categoryService;

    /**
     * 카테고리 관리 페이지
     */
    @GetMapping
    public String categories(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            Model model) {
        if (type != null) {
            model.addAttribute("type", type);
        }
        if (status != null) {
            model.addAttribute("status", status);
        }

        // 카테고리 통계 정보 추가
        Map<String, Long> summary = new HashMap<>();
        List<CategoryDTO> allCategories = categoryService.getAllCategories();

        summary.put("totalCategories", (long) allCategories.size());
        summary.put("activeCategories", allCategories.stream()
                .filter(CategoryDTO::isActive)
                .count());
        summary.put("totalProducts", allCategories.stream()
                .mapToLong(CategoryDTO::getProductCount)
                .sum());
        summary.put("maxDepth", (long) allCategories.stream()
                .mapToInt(CategoryDTO::getDepth)
                .max()
                .orElse(0));

        model.addAttribute("summary", summary);
        model.addAttribute("categories", allCategories);

        return "admin/categories/categories";
    }

    /**
     * 카테고리 등록 페이지
     */
    @GetMapping("/new")
    public String createCategory(
            @RequestParam(required = false) String type,
            Model model) {
        if (type != null) {
            model.addAttribute("type", type);
        }
        return "admin/categories/category-form";
    }

    /**
     * 카테고리 수정 페이지
     */
    @GetMapping("/{id}/edit")
    public String editCategory(@PathVariable Long id, Model model) {
        model.addAttribute("categoryId", id);
        return "admin/categories/category-form";
    }

    /**
     * 카테고리 상세 페이지
     */
    @GetMapping("/{id}")
    public String categoryDetail(@PathVariable Long id, Model model) {
        model.addAttribute("categoryId", id);
        return "admin/categories/category-detail";
    }

    /**
     * 카테고리 순서 관리 페이지
     */
    @GetMapping("/order")
    public String categoryOrder(
            @RequestParam(required = false) String type,
            Model model) {
        if (type != null) {
            model.addAttribute("type", type);
        }
        return "admin/categories/order";
    }

    /**
     * 카테고리 통계 페이지
     */
    @GetMapping("/statistics")
    public String categoryStatistics(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String type,
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
        if (type != null) {
            model.addAttribute("type", type);
        }
        return "admin/categories/statistics";
    }
}

