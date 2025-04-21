package com.peakmeshop.shop.controller;

import com.peakmeshop.api.dto.BrandDTO;
import com.peakmeshop.api.dto.CategoryDTO;
import com.peakmeshop.api.dto.ProductDTO;
import com.peakmeshop.domain.entity.Product;
import com.peakmeshop.domain.service.BrandService;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 쇼핑몰 메인 페이지, 카테고리, 검색 등 기본 쇼핑 기능 관련 뷰 컨트롤러
 */
@Controller
@RequiredArgsConstructor
public class MainViewController {
    private final CategoryService categoryService;
    private final ProductService productService;
    private final BrandService brandService;

    /**
     * 쇼핑몰 메인 페이지
     */
    @GetMapping
    public String shopIndex(Model model) {
        try {
            // 메인 페이지에 표시할 카테고리와 상품 수 설정
            int featuredItemCount = 8;
            
            // 카테고리 로드
            List<CategoryDTO> categories = categoryService.getFeaturedCategories();
            model.addAttribute("categories", categories);

            // 신상품 로드 (최신순)
            Page<ProductDTO> newProducts = productService.getNewArrivals(
                PageRequest.of(0, featuredItemCount, Sort.by(Sort.Direction.DESC, "createdAt"))
            );
            model.addAttribute("newProducts", newProducts.getContent());

            // 베스트셀러 로드 (판매량순)
            Page<ProductDTO> bestProducts = productService.getBestSellers(
                PageRequest.of(0, featuredItemCount, Sort.by(Sort.Direction.DESC, "salesCount"))
            );
            model.addAttribute("bestProducts", bestProducts.getContent());

            // 할인상품 로드 (할인율순)
            Page<ProductDTO> discountedProducts = productService.getDiscountedProducts(
                PageRequest.of(0, featuredItemCount, Sort.by(Sort.Direction.DESC, "discountRate"))
            );
            model.addAttribute("discountedProducts", discountedProducts.getContent());

            // 인기 브랜드 로드 (팔로워순)
            List<BrandDTO> brands = brandService.getFeaturedBrands();
            model.addAttribute("brands", brands);

            return "shop/index.html/index";
        } catch (Exception e) {
            // 에러 로깅
            e.printStackTrace();
            model.addAttribute("error", "페이지를 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 카테고리별 상품 목록
     */
    @GetMapping("/category/{categoryId}")
    public String categoryProducts(
            @PathVariable Long categoryId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) List<Long> brandIds,
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
                categoryId, minPrice, maxPrice, brandIds, pageable
            );
            model.addAttribute("products", products);

            // 필터 옵션 로드
            model.addAttribute("brands", brandService.getBrandsByCategory(categoryId));
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
            model.addAttribute("selectedBrandIds", brandIds);
            model.addAttribute("currentSort", sort);

            return "shop/product-list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "상품 목록을 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 상품 검색 결과
     */
    @GetMapping("/search")
    public String searchProducts(
            @RequestParam String query,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) List<Long> brandIds,
            Model model) {
        try {
            // 검색어 검증
            if (query == null || query.trim().isEmpty()) {
                return "redirect:/";
            }
            model.addAttribute("searchQuery", query);

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
                    case "relevance":
                        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), 
                            Sort.by(Sort.Direction.DESC, "searchScore"));
                        break;
                }
            }

            // 검색 결과 로드
            Page<ProductDTO> products = productService.searchProducts(
                query, categoryId, minPrice, maxPrice, brandIds, pageable
            );
            model.addAttribute("products", products);

            // 필터 옵션 로드
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("brands", brandService.getAllBrands());
            model.addAttribute("selectedCategoryId", categoryId);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
            model.addAttribute("selectedBrandIds", brandIds);
            model.addAttribute("currentSort", sort);

            return "shop/product-list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "검색 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 베스트셀러 상품 목록
     */
    @GetMapping("/best-sellers")
    public String bestSellers(
            @PageableDefault(size = 20, sort = "soldCount", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        try {
            Page<ProductDTO> products = productService.getBestSellers(pageable);
            List<CategoryDTO> categories = categoryService.getAllCategories();
            List<BrandDTO> brands = brandService.getAllBrands();

            model.addAttribute("products", products);
            model.addAttribute("categories", categories);
            model.addAttribute("brands", brands);
            model.addAttribute("title", "베스트셀러");

            return "shop/product/list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "페이지를 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 신상품 목록
     */
    @GetMapping("/new-arrivals")
    public String newArrivals(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        try {
            Page<ProductDTO> products = productService.getNewArrivals(pageable);
            List<CategoryDTO> categories = categoryService.getAllCategories();
            List<BrandDTO> brands = brandService.getAllBrands();

            model.addAttribute("products", products);
            model.addAttribute("categories", categories);
            model.addAttribute("brands", brands);
            model.addAttribute("title", "신상품");

            return "shop/product/list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "페이지를 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 할인 상품 목록
     */
    @GetMapping("/on-sale")
    public String onSale(
            @PageableDefault(size = 20, sort = "discountRate", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        try {
            Page<ProductDTO> products = productService.getDiscountedProducts(pageable);
            List<CategoryDTO> categories = categoryService.getAllCategories();
            List<BrandDTO> brands = brandService.getAllBrands();

            model.addAttribute("products", products);
            model.addAttribute("categories", categories);
            model.addAttribute("brands", brands);
            model.addAttribute("title", "할인 상품");

            return "shop/product/list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "페이지를 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 브랜드별 상품 목록
     */
    @GetMapping("/brand/{brandId}")
    public String brandProducts(
            @PathVariable Long brandId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            Model model) {
        try {
            // 정렬 조건 처리
            if (sort != null) {
                switch (sort) {
                    case "price_asc":
                        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                                Sort.by("price").ascending());
                        break;
                    case "price_desc":
                        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                                Sort.by("price").descending());
                        break;
                    case "name_asc":
                        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                                Sort.by("name").ascending());
                        break;
                    case "name_desc":
                        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                                Sort.by("name").descending());
                        break;
                }
            }

            Page<ProductDTO> products = productService.getProductsByBrand(brandId, pageable);
            BrandDTO brand = brandService.getBrandById(brandId).orElseThrow();
            List<CategoryDTO> categories = categoryService.getAllCategories();
            List<BrandDTO> brands = brandService.getAllBrands();

            model.addAttribute("products", products);
            model.addAttribute("brand", brand);
            model.addAttribute("categories", categories);
            model.addAttribute("brands", brands);
            model.addAttribute("title", brand.getName() + " 상품");
            model.addAttribute("sort", sort);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);

            return "shop/product/list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "페이지를 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 상품 비교 페이지
     */
    @GetMapping("/compare")
    public String compareProducts(
            @RequestParam List<Long> productIds,
            Model model) {
        try {
            if (productIds == null || productIds.isEmpty()) {
                return "redirect:/";
            }

            // 비교할 상품 정보 로드
            List<Product> products = productService.getProductsByIds(productIds);
            model.addAttribute("products", products);

            return "shop/product-compare";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "상품 비교 중 오류가 발생했습니다.");
            return "error/500";
        }
    }
}

