package com.peakmeshop.shop.controller;

import com.peakmeshop.api.dto.CategoryDTO;
import com.peakmeshop.api.dto.ProductDTO;
import com.peakmeshop.api.dto.ReviewDTO;
import com.peakmeshop.domain.service.CategoryService;
import com.peakmeshop.domain.service.ProductService;
import com.peakmeshop.domain.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

/**
 * 상품 관련 뷰 컨트롤러
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
@Slf4j
public class ProductViewController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ReviewService reviewService;

    /**
     * 상품 목록 페이지
     */
    @GetMapping("/list")
    public String productList(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        Page<ProductDTO> products;

        if (keyword != null && !keyword.isEmpty()) {
            // 검색어로 상품 조회
            products = productService.searchProducts(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else if (categoryId != null) {
            // 카테고리로 상품 조회
            products = productService.getProductsByCategory(categoryId, pageable);
            CategoryDTO category = categoryService.getCategoryById(categoryId);
            model.addAttribute("category", category);
        } else {
            // 모든 상품 조회
            products = productService.getAllProducts(pageable);
        }

        // 카테고리 목록 조회
        List<CategoryDTO> categories = categoryService.getAllCategories();

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);

        return "shop/product/product-list";
    }

    /**
     * 상품 상세 페이지
     */
    @GetMapping("/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        ProductDTO product = productService.getProductById(id);

        // 연관 상품 조회 (같은 카테고리의 다른 상품)
        Page<ProductDTO> relatedProducts = null;
        if (product.getCategoryId() != null) {
            relatedProducts = productService.getProductsByCategory(
                    product.getCategoryId(),
                    PageRequest.of(0, 4, Sort.by("createdAt").descending())
            );
        }

        // 리뷰 요약 정보 조회
        Page<ReviewDTO> recentReviews = reviewService.getReviewsByProductId(
                id,
                PageRequest.of(0, 3, Sort.by("createdAt").descending())
        );

        // 평균 평점 계산 (리뷰 서비스에서 제공하지 않는 경우 직접 계산)
        double averageRating = 0.0;
        if (recentReviews != null && !recentReviews.isEmpty()) {
            averageRating = recentReviews.getContent().stream()
                    .mapToInt(ReviewDTO::getRating)
                    .average()
                    .orElse(0.0);
        }

        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("recentReviews", recentReviews);
        model.addAttribute("averageRating", averageRating);

        return "shop/product/product-detail";
    }

    /**
     * 상품 리뷰 목록 페이지
     */
    @GetMapping("/{id}/reviews")
    public String productReviews(
            @PathVariable Long id,
            @RequestParam(defaultValue = "recent") String sort,
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {

        ProductDTO product = productService.getProductById(id);
        Page<ReviewDTO> reviews;

        // 정렬 방식에 따라 리뷰 조회
        Pageable sortedPageable;
        switch (sort) {
            case "rating-high":
                sortedPageable = PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by("rating").descending()
                );
                break;
            case "rating-low":
                sortedPageable = PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by("rating").ascending()
                );
                break;
            case "helpful":
                sortedPageable = PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by("helpfulCount").descending()
                );
                break;
            case "recent":
            default:
                sortedPageable = PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by("createdAt").descending()
                );
                break;
        }

        reviews = reviewService.getReviewsByProductId(id, sortedPageable);

        // 평균 평점 계산 (리뷰 서비스에서 제공하지 않는 경우 직접 계산)
        double averageRating = 0.0;
        if (reviews != null && !reviews.isEmpty()) {
            averageRating = reviews.getContent().stream()
                    .mapToInt(ReviewDTO::getRating)
                    .average()
                    .orElse(0.0);
        }

        model.addAttribute("product", product);
        model.addAttribute("reviews", reviews);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("productId", id);
        model.addAttribute("sort", sort);

        return "shop/product/product-reviews";
    }

    /**
     * 리뷰 작성 페이지
     */
    @GetMapping("/{id}/review")
    public String reviewForm(
            @PathVariable Long id,
            @RequestParam(required = false) Long orderId,
            Model model,
            Authentication authentication) {

        // 로그인 상태 확인
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login?redirectUrl=/shop/product/" + id + "/review";
        }

        ProductDTO product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("productId", id);

        if (orderId != null) {
            model.addAttribute("orderId", orderId);
        }

        return "shop/product/review-form";
    }

    /**
     * 카테고리별 상품 목록
     */
    @GetMapping("/category/{categoryName}")
    public String categoryProducts(
            @PathVariable String categoryName,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        // 카테고리 슬러그로 카테고리 정보 조회
        Optional<CategoryDTO> categoryOpt = categoryService.getCategoryBySlug(categoryName);

        if (categoryOpt.isPresent()) {
            CategoryDTO category = categoryOpt.get();
            // 카테고리 ID로 상품 목록 조회
            Page<ProductDTO> products = productService.getProductsByCategory(category.getId(), pageable);

            model.addAttribute("products", products);
            model.addAttribute("category", category);

            // 모든 카테고리 목록 조회 (사이드바용)
            List<CategoryDTO> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
        } else {
            // 카테고리가 존재하지 않는 경우 전체 상품 목록으로 리다이렉트
            return "redirect:/shop/product/list";
        }

        return "shop/product/product-list";
    }

    /**
     * 상품 검색 결과
     */
    @GetMapping("/search")
    public String searchProducts(
            @RequestParam String query,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        // 검색어로 상품 검색
        Page<ProductDTO> searchResults = productService.searchProducts(query, pageable);

        model.addAttribute("products", searchResults);
        model.addAttribute("keyword", query);
        model.addAttribute("searchQuery", query);

        // 모든 카테고리 목록 조회 (사이드바용)
        List<CategoryDTO> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        return "shop/product/product-list";
    }

    /**
     * 신상품 목록
     */
    @GetMapping("/new-arrivals")
    public String newArrivals(
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        // 신상품 목록 조회
        Page<ProductDTO> newProducts = productService.getNewArrivals(pageable);

        model.addAttribute("products", newProducts);
        model.addAttribute("listType", "new");
        model.addAttribute("pageTitle", "신상품");

        // 모든 카테고리 목록 조회 (사이드바용)
        List<CategoryDTO> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        return "shop/product/product-list";
    }

    /**
     * 베스트셀러 목록
     */
    @GetMapping("/best-sellers")
    public String bestSellers(
            @PageableDefault(size = 12, sort = "salesCount", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        // 베스트셀러 목록 조회
        Page<ProductDTO> bestProducts = productService.getBestSellers(pageable);

        model.addAttribute("products", bestProducts);
        model.addAttribute("listType", "best");
        model.addAttribute("pageTitle", "베스트셀러");

        // 모든 카테고리 목록 조회 (사이드바용)
        List<CategoryDTO> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        return "shop/product/product-list";
    }

    /**
     * 세일 상품 목록
     */
    @GetMapping("/sale")
    public String saleProducts(
            @PageableDefault(size = 12, sort = "discount", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        // 할인 상품 목록 조회
        Page<ProductDTO> saleProducts = productService.getDiscountedProducts(pageable);

        model.addAttribute("products", saleProducts);
        model.addAttribute("listType", "sale");
        model.addAttribute("pageTitle", "세일 상품");

        // 모든 카테고리 목록 조회 (사이드바용)
        List<CategoryDTO> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        return "shop/product/product-list";
    }

    /**
     * 브랜드별 상품 목록
     */
    @GetMapping("/brand/{brandName}")
    public String brandProducts(
            @PathVariable String brandName,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        // 브랜드명으로 상품 목록 조회
        Page<ProductDTO> brandProducts = productService.getProductsByBrand(brandName, pageable);

        model.addAttribute("products", brandProducts);
        model.addAttribute("brandName", brandName);
        model.addAttribute("pageTitle", brandName + " 상품");

        // 모든 카테고리 목록 조회 (사이드바용)
        List<CategoryDTO> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        return "shop/product/product-list";
    }
}

