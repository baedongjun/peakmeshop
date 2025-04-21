package com.peakmeshop.shop.controller;

import com.peakmeshop.api.dto.BrandDTO;
import com.peakmeshop.api.dto.BrandNewsDTO;
import com.peakmeshop.api.dto.CategoryDTO;
import com.peakmeshop.api.dto.ProductDTO;
import com.peakmeshop.domain.service.BrandService;
import com.peakmeshop.domain.service.CategoryService;
import com.peakmeshop.domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 브랜드 관련 뷰 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandViewController {
    private final BrandService brandService;
    private final ProductService productService;
    private final CategoryService categoryService;

    /**
     * 브랜드 목록
     */
    @GetMapping
    public String brandList(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            Model model) {
        try {
            // 브랜드 목록 로드
            Page<BrandDTO> brands = brandService.getBrands(category, keyword, pageable);
            model.addAttribute("brands", brands);

            // 브랜드 카테고리 목록
            model.addAttribute("categories", brandService.getBrandCategories());
            model.addAttribute("selectedCategory", category);
            model.addAttribute("keyword", keyword);

            return "shop/brand/list";
        } catch (Exception e) {
            log.error("브랜드 목록 조회 중 오류 발생", e);
            model.addAttribute("error", "브랜드 목록을 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 브랜드 상세
     */
    @GetMapping("/{id}")
    public String brandDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        try {
            // 브랜드 정보 로드
            BrandDTO brand = brandService.getBrandById(id);
            if (brand == null) {
                return "error/404";
            }
            model.addAttribute("brand", brand);

            // 조회수 증가
            brandService.incrementViewCount(id);

            // 브랜드 팔로우 여부 확인
            if (userDetails != null) {
                boolean isFollowing = brandService.isFollowing(userDetails.getUsername(), id);
                model.addAttribute("isFollowing", isFollowing);
            }

            // 인기 상품 로드
            Page<ProductDTO> popularProducts = productService.getProductsByBrand(
                id, "popularity", PageRequest.of(0, 8)
            );
            model.addAttribute("popularProducts", popularProducts.getContent());

            // 신상품 로드
            Page<ProductDTO> newProducts = productService.getProductsByBrand(
                id, "new", PageRequest.of(0, 8)
            );
            model.addAttribute("newProducts", newProducts.getContent());

            return "shop/brand/detail";
        } catch (Exception e) {
            log.error("브랜드 정보 로드 중 오류 발생", e);
            model.addAttribute("error", "브랜드 정보를 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 브랜드 상품 목록
     */
    @GetMapping("/{brandId}/products")
    public String brandProducts(
            @PathVariable Long brandId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            Model model) {
        try {
            // 브랜드 정보 로드
            BrandDTO brand = brandService.getBrandById(brandId);
            if (brand == null) {
                return "error/404";
            }
            model.addAttribute("brand", brand);

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

            // 브랜드 상품 목록 로드
            Page<ProductDTO> products = productService.getProductsByBrand(
                brandId, categoryId, minPrice, maxPrice, pageable
            );
            model.addAttribute("products", products);

            // 필터 옵션 로드
            model.addAttribute("categories", brandService.getBrandCategories());
            model.addAttribute("selectedCategoryId", categoryId);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
            model.addAttribute("currentSort", sort);

            return "shop/brand/products";
        } catch (Exception e) {
            log.error("상품 목록 로드 중 오류 발생", e);
            model.addAttribute("error", "상품 목록을 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 브랜드 소식
     */
    @GetMapping("/{brandId}/news")
    public String brandNews(
            @PathVariable Long brandId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        try {
            // 브랜드 정보 로드
            BrandDTO brand = brandService.getBrandById(brandId);
            if (brand == null) {
                return "error/404";
            }
            model.addAttribute("brand", brand);

            // 브랜드 소식 로드
            Page<BrandNewsDTO> news = brandService.getBrandNews(brandId, pageable);
            model.addAttribute("news", news);

            return "shop/brand/news";
        } catch (Exception e) {
            log.error("브랜드 소식 로드 중 오류 발생", e);
            model.addAttribute("error", "브랜드 소식을 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 브랜드 소식 상세
     */
    @GetMapping("/{brandId}/news/{newsId}")
    public String brandNewsDetail(
            @PathVariable Long brandId,
            @PathVariable Long newsId,
            Model model) {
        try {
            // 브랜드 정보 로드
            BrandDTO brand = brandService.getBrandById(brandId);
            if (brand == null) {
                return "error/404";
            }
            model.addAttribute("brand", brand);

            // 브랜드 소식 로드
            BrandNewsDTO news = brandService.getBrandNewsById(newsId);
            if (news == null || !news.getBrandId().equals(brandId)) {
                return "error/404";
            }
            model.addAttribute("news", news);

            // 조회수 증가
            brandService.incrementNewsViewCount(newsId);

            // 이전글, 다음글
            model.addAttribute("prevNews", brandService.getPrevBrandNews(brandId, newsId));
            model.addAttribute("nextNews", brandService.getNextBrandNews(brandId, newsId));

            return "shop/brand/news-detail";
        } catch (Exception e) {
            log.error("브랜드 소식 상세 로드 중 오류 발생", e);
            model.addAttribute("error", "브랜드 소식을 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }


} 