package com.peakmeshop.shop.controller;

import com.peakmeshop.api.dto.ProductDTO;
import com.peakmeshop.api.dto.ReviewDTO;
import com.peakmeshop.domain.service.BrandService;
import com.peakmeshop.domain.service.CategoryService;
import com.peakmeshop.domain.service.ProductService;
import com.peakmeshop.domain.service.ReviewService;
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
import java.util.Map;

/**
 * 상품 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductViewController {
    private final ProductService productService;
    private final ReviewService reviewService;
    private final CategoryService categoryService;
    private final BrandService brandService;

    /**
     * 상품 목록
     */
    @GetMapping("/list")
    public String productList(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Boolean onSale,
            Model model) {
        try {
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
            Page<ProductDTO> products = productService.getProducts(
                categoryId, brandId, minPrice, maxPrice, onSale, pageable
            );
            model.addAttribute("products", products);

            // 필터 옵션 로드
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("brands", brandService.getAllBrands());
            model.addAttribute("selectedCategoryId", categoryId);
            model.addAttribute("selectedBrandId", brandId);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
            model.addAttribute("onSale", onSale);
            model.addAttribute("currentSort", sort);

            return "shop/product/list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "상품 목록을 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 상품 상세 페이지
     */
    @GetMapping("/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        try {
            // 상품 정보 로드
            ProductDTO product = productService.getProductById(id);
            if (product == null) {
                return "error/404";
            }
            model.addAttribute("product", product);

            // 상품 조회수 증가
            productService.incrementViewCount(id);

            // 관련 상품 로드
            List<ProductDTO> relatedProducts = productService.getRelatedProducts(id, 8);
            model.addAttribute("relatedProducts", relatedProducts);

            // 최근 리뷰 로드
            Page<ReviewDTO> reviews = reviewService.getProductReviews(
                id, PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"))
            );
            model.addAttribute("recentReviews", reviews.getContent());

            // 리뷰 통계 로드
            Map<String, Object> reviewStats = reviewService.getProductReviewStats(id);
            model.addAttribute("reviewStats", reviewStats);

            return "shop/product/detail";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "상품 정보를 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 상품 리뷰 작성 페이지
     */
    @GetMapping("/{productId}/review")
    public String writeReview(
            @PathVariable Long productId,
            @RequestParam(required = false) Long orderId,
            Model model) {
        try {
            // 상품 정보 로드
            ProductDTO product = productService.getProductById(productId);
            if (product == null) {
                return "error/404";
            }
            model.addAttribute("product", product);
            model.addAttribute("orderId", orderId);

            // 이미 작성한 리뷰가 있는지 확인
            if (orderId != null) {
                boolean hasReview = reviewService.hasOrderReview(orderId);
                if (hasReview) {
                    return "redirect:/mypage/reviews?error=already_exists";
                }
            }

            return "shop/review-form";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "리뷰 작성 페이지를 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 상품 리뷰 목록
     */
    @GetMapping("/{productId}/reviews")
    public String productReviews(
            @PathVariable Long productId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Boolean hasPhoto,
            Model model) {
        try {
            // 상품 정보 로드
            ProductDTO product = productService.getProductById(productId);
            if (product == null) {
                return "error/404";
            }
            model.addAttribute("product", product);

            // 정렬 조건 적용
            if (sort != null) {
                switch (sort) {
                    case "rating_desc":
                        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), 
                            Sort.by(Sort.Direction.DESC, "rating"));
                        break;
                    case "rating_asc":
                        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), 
                            Sort.by(Sort.Direction.ASC, "rating"));
                        break;
                    case "helpful":
                        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), 
                            Sort.by(Sort.Direction.DESC, "helpfulCount"));
                        break;
                }
            }

            // 리뷰 목록 로드
            Page<ReviewDTO> reviews = reviewService.getProductReviews(
                productId, rating, hasPhoto, pageable
            );
            model.addAttribute("reviews", reviews);

            // 리뷰 통계 로드
            Map<String, Object> reviewStats = reviewService.getProductReviewStats(productId);
            model.addAttribute("reviewStats", reviewStats);

            // 필터 옵션
            model.addAttribute("selectedRating", rating);
            model.addAttribute("hasPhoto", hasPhoto);
            model.addAttribute("currentSort", sort);

            return "shop/product-reviews";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "리뷰 목록을 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 관련 상품 목록
     */
    @GetMapping("/{productId}/related")
    public String relatedProducts(
            @PathVariable Long productId,
            @PageableDefault(size = 8) Pageable pageable,
            Model model) {
        try {
            ProductDTO product = productService.getProductById(productId);
            Page<ProductDTO> relatedProducts = productService.getRelatedProducts(productId, pageable);

            model.addAttribute("product", product);
            model.addAttribute("relatedProducts", relatedProducts);

            return "shop/product/related";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "관련 상품을 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 함께 구매한 상품 목록
     */
    @GetMapping("/{productId}/frequently-bought-together")
    public String frequentlyBoughtTogether(
            @PathVariable Long productId,
            @PageableDefault(size = 8) Pageable pageable,
            Model model) {
        try {
            ProductDTO product = productService.getProductById(productId);
            Page<ProductDTO> frequentlyBoughtProducts = productService.getFrequentlyBoughtTogether(productId, pageable);

            model.addAttribute("product", product);
            model.addAttribute("frequentlyBoughtProducts", frequentlyBoughtProducts);

            return "shop/product/frequently-bought";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "함께 구매한 상품을 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    @GetMapping("/brands")
    public String brandList(Model model) {
        try {
            // ... existing code ...
            return "shop/product/brands";
        } catch (Exception e) {
            // ... existing code ...
        }
    }
}

