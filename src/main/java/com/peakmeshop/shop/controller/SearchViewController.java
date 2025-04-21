package com.peakmeshop.shop.controller;

import com.peakmeshop.api.dto.BrandDTO;
import com.peakmeshop.api.dto.CategoryDTO;
import com.peakmeshop.api.dto.ProductDTO;
import com.peakmeshop.domain.service.BrandService;
import com.peakmeshop.domain.service.CategoryService;
import com.peakmeshop.domain.service.ProductService;
import com.peakmeshop.domain.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 검색 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchViewController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final SearchService searchService;

    /**
     * 통합 검색
     */
    @GetMapping
    public String search(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) List<Long> brandIds,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            Model model) {
        try {
            // 검색어 유효성 검사
            if (keyword == null || keyword.trim().isEmpty()) {
                return "redirect:/";
            }
            model.addAttribute("keyword", keyword);

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

            // 상품 검색 결과 로드
            Page<ProductDTO> products = searchService.searchProducts(
                keyword, categoryId, brandIds, minPrice, maxPrice, pageable
            );
            model.addAttribute("products", products);

            // 카테고리 검색 결과 로드
            List<CategoryDTO> categories = searchService.searchCategories(keyword);
            model.addAttribute("categories", categories);

            // 브랜드 검색 결과 로드
            List<BrandDTO> brands = searchService.searchBrands(keyword);
            model.addAttribute("brands", brands);

            // 필터 옵션 로드
            model.addAttribute("allCategories", categoryService.getAllCategories());
            model.addAttribute("allBrands", brandService.getAllBrands());
            model.addAttribute("selectedCategoryId", categoryId);
            model.addAttribute("selectedBrandIds", brandIds);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
            model.addAttribute("currentSort", sort);

            return "shop/search/index";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "검색 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 인기 검색어
     */
    @GetMapping("/popular")
    public String popularKeywords(Model model) {
        try {
            // 실시간 인기 검색어
            List<String> realtimeKeywords = searchService.getRealtimePopularKeywords();
            model.addAttribute("realtimeKeywords", realtimeKeywords);

            // 일간 인기 검색어
            List<String> dailyKeywords = searchService.getDailyPopularKeywords();
            model.addAttribute("dailyKeywords", dailyKeywords);

            // 주간 인기 검색어
            List<String> weeklyKeywords = searchService.getWeeklyPopularKeywords();
            model.addAttribute("weeklyKeywords", weeklyKeywords);

            return "shop/search/popular";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "인기 검색어를 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 검색어 자동완성
     */
    @GetMapping("/suggest")
    public String suggestKeywords(
            @RequestParam String keyword,
            Model model) {
        try {
            // 검색어 자동완성 결과 로드
            List<String> suggestions = searchService.getSuggestKeywords(keyword);
            model.addAttribute("suggestions", suggestions);

            return "shop/search/suggest";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "검색어 자동완성 중 오류가 발생했습니다.");
            return "error/500";
        }
    }
} 