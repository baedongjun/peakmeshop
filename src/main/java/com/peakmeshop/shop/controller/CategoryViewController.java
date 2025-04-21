package com.peakmeshop.shop.controller;

import com.peakmeshop.api.dto.CategoryDTO;
import com.peakmeshop.api.dto.ProductDTO;
import com.peakmeshop.domain.service.CategoryService;
import com.peakmeshop.domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 카테고리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryViewController {
    private final CategoryService categoryService;
    private final ProductService productService;

    /**
     * 카테고리 목록
     */
    @GetMapping
    public String categoryList(Model model) {
        try {
            // 카테고리 목록 로드
            List<CategoryDTO> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);

            // 인기 카테고리 로드
            List<CategoryDTO> popularCategories = categoryService.getPopularCategories();
            model.addAttribute("popularCategories", popularCategories);

            return "shop/category/list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "카테고리 목록을 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 카테고리 상세
     */
    @GetMapping("/{id}")
    public String categoryDetail(@PathVariable Long id, Model model) {
        try {
            // 카테고리 정보 로드
            CategoryDTO category = categoryService.getCategoryById(id);
            if (category == null) {
                return "error/404";
            }
            model.addAttribute("category", category);

            // 서브 카테고리 로드
            List<CategoryDTO> subCategories = categoryService.getSubCategories(id);
            model.addAttribute("subCategories", subCategories);

            // 인기 상품 로드
            Page<ProductDTO> popularProducts = productService.getProductsByCategory(
                id, "popularity", PageRequest.of(0, 8)
            );
            model.addAttribute("popularProducts", popularProducts.getContent());

            // 신상품 로드
            Page<ProductDTO> newProducts = productService.getProductsByCategory(
                id, "new", PageRequest.of(0, 8)
            );
            model.addAttribute("newProducts", newProducts.getContent());

            return "shop/category/detail";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "카테고리 정보를 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 카테고리 상품 목록
     */
    @GetMapping("/{categoryId}/products")
    public String categoryProducts(
            @PathVariable Long categoryId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) List<Long> brandIds,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            Model model) {
        try {
            // 카테고리 정보 로드
            CategoryDTO category = categoryService.getCategoryById(categoryId);
            if (category == null) {
                return "error/404";
            }
            model.addAttribute("category", category);

            // 정렬 조건 적용
            if (sort != null) {
                switch (sort) {
                    case "price_asc":
                        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), 
                            Sort.by(Sort.Direction.ASC, "price"));
                        break;
                    case "price_desc":
                        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), 
                            Sort.by(Sort.Direction.DESC, "price"));
                        break;
                    case "popularity":
                        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), 
                            Sort.by(Sort.Direction.DESC, "viewCount"));
                        break;
                    case "rating":
                        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), 
                            Sort.by(Sort.Direction.DESC, "averageRating"));
                        break;
                }
            }

            // 상품 목록 로드
            Page<ProductDTO> products = productService.getProductsByCategory(
                categoryId, brandIds, minPrice, maxPrice, pageable
            );
            model.addAttribute("products", products);

            // 필터 옵션 로드
            model.addAttribute("brands", categoryService.getCategoryBrands(categoryId));
            model.addAttribute("selectedBrandIds", brandIds);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
            model.addAttribute("currentSort", sort);

            return "shop/category/products";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "상품 목록을 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }
} 