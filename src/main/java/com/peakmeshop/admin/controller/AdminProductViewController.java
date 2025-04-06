package com.peakmeshop.admin.controller;

import com.peakmeshop.domain.service.CategoryService;
import com.peakmeshop.domain.service.ProductService;
import com.peakmeshop.domain.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 관리자 상품 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminProductViewController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final SupplierService supplierService;

    /**
     * 상품 관리 페이지
     */
    @GetMapping("/products")
    public String products(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {
        if (category != null) {
            model.addAttribute("category", category);
        }
        if (status != null) {
            model.addAttribute("status", status);
        }

        model.addAttribute("summary", productService.getProductSummary());
        model.addAttribute("products", productService.getAllProducts(pageable));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("suppliers", supplierService.getAllSuppliers(pageable));

        return "admin/products/products";
    }

    /**
     * 상품 등록 페이지
     */
    @GetMapping("/products/new")
    public String createProduct(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("suppliers", supplierService.getAllSuppliers(Pageable.unpaged()));
        return "admin/products/form";
    }

    /**
     * 상품 수정 페이지
     */
    @GetMapping("/products/{id}/edit")
    public String editProduct(@PathVariable Long id, Model model) {
        model.addAttribute("productId", id);
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("suppliers", supplierService.getAllSuppliers(Pageable.unpaged()));
        return "admin/products/form";
    }

    /**
     * 상품 상세 페이지
     */
    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        model.addAttribute("productId", id);
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("summary", productService.getProductSummary(id));
        return "admin/products/detail";
    }

    /**
     * 상품 통계 페이지
     */
    @GetMapping("/products/statistics")
    public String productStatistics(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String category,
            Model model) {
        model.addAttribute("period", period);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("category", category);
        model.addAttribute("statistics", productService.getProductStatistics(period, startDate, endDate, category));
        return "admin/products/statistics";
    }

    /**
     * 상품 리뷰 관리 페이지
     */
    @GetMapping("/products/reviews")
    public String productReviews(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {
        if (status != null) {
            model.addAttribute("status", status);
        }
        model.addAttribute("reviews", productService.getProductReviews(pageable));
        return "admin/products/reviews";
    }

    /**
     * 상품 리뷰 상세 페이지
     */
    @GetMapping("/products/reviews/{id}")
    public String reviewDetail(@PathVariable Long id, Model model) {
        model.addAttribute("reviewId", id);
        model.addAttribute("review", productService.getReviewById(id));
        return "admin/products/review-detail";
    }

    /**
     * 상품 문의 관리 페이지
     */
    @GetMapping("/products/qnas")
    public String productQnas(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {
        if (status != null) {
            model.addAttribute("status", status);
        }
        model.addAttribute("qnas", productService.getProductQnas(pageable));
        return "admin/products/qnas";
    }

    /**
     * 상품 문의 상세 페이지
     */
    @GetMapping("/products/qnas/{id}")
    public String qnaDetail(@PathVariable Long id, Model model) {
        model.addAttribute("qnaId", id);
        model.addAttribute("qna", productService.getQnaById(id));
        return "admin/products/qna-detail";
    }

    /**
     * 상품 재고 관리 페이지
     */
    @GetMapping("/products/inventory")
    public String productInventory(
            @RequestParam(required = false) String category,
            Model model) {
        if (category != null) {
            model.addAttribute("category", category);
        }
        model.addAttribute("inventory", productService.getInventory(category));
        return "admin/products/inventory";
    }

    /**
     * 상품 옵션 관리 페이지
     */
    @GetMapping("/products/{id}/options")
    public String productOptions(@PathVariable Long id, Model model) {
        model.addAttribute("productId", id);
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("options", productService.getProductOptions(id));
        return "admin/products/options";
    }
}

