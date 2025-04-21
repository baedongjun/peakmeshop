package com.peakmeshop.domain.service;

import com.peakmeshop.api.dto.BrandDTO;
import com.peakmeshop.api.dto.CategoryDTO;
import com.peakmeshop.api.dto.ProductDTO;
import com.peakmeshop.api.dto.SearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 검색 관련 서비스 인터페이스
 */
public interface SearchService {
    /**
     * 상품 검색
     */
    Page<ProductDTO> searchProducts(
        String keyword,
        Long categoryId,
        List<Long> brandIds,
        Integer minPrice,
        Integer maxPrice,
        Pageable pageable
    );

    /**
     * 카테고리 검색
     */
    List<CategoryDTO> searchCategories(String keyword);

    /**
     * 브랜드 검색
     */
    List<BrandDTO> searchBrands(String keyword);

    /**
     * 실시간 인기 검색어 조회
     */
    List<String> getRealtimePopularKeywords();

    /**
     * 일간 인기 검색어 조회
     */
    List<String> getDailyPopularKeywords();

    /**
     * 주간 인기 검색어 조회
     */
    List<String> getWeeklyPopularKeywords();

    /**
     * 검색어 자동완성
     */
    List<String> getSuggestKeywords(String keyword);

    /**
     * 검색 DTO를 이용한 상품 검색
     */
    Page<ProductDTO> search(SearchDTO searchDTO, Pageable pageable);

    /**
     * 키워드로 상품 검색
     */
    Page<ProductDTO> searchByKeyword(String keyword, Pageable pageable);

    /**
     * 카테고리별 상품 검색
     */
    Page<ProductDTO> searchByCategory(Long categoryId, Pageable pageable);

    /**
     * 브랜드별 상품 검색
     */
    Page<ProductDTO> searchByBrand(Long brandId, Pageable pageable);

    /**
     * 가격 범위로 상품 검색
     */
    Page<ProductDTO> searchByPriceRange(Double minPrice, Double maxPrice, Pageable pageable);

    /**
     * 속성으로 상품 검색
     */
    Page<ProductDTO> searchByAttribute(String name, String value, Pageable pageable);

    /**
     * 재고 여부로 상품 검색
     */
    Page<ProductDTO> searchInStock(boolean inStock, Pageable pageable);

    /**
     * 할인 여부로 상품 검색
     */
    Page<ProductDTO> searchOnSale(boolean onSale, Pageable pageable);

    /**
     * 모든 상품 인덱싱
     */
    void indexAllProducts();

    /**
     * 특정 상품 인덱싱
     */
    void indexProduct(Long productId);

    /**
     * 검색 인덱스에서 상품을 제거합니다.
     */
    void removeProductFromIndex(Long productId);
}