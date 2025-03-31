package com.peakmeshop.domain.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.ProductDTO;
import com.peakmeshop.api.dto.ProductSearchDTO;

public interface ProductSearchService {

    /**
     * 상품 검색
     * @param searchDTO 검색 조건
     * @param pageable 페이징 정보
     * @return 검색 결과
     */
    Page<ProductDTO> searchProducts(ProductSearchDTO searchDTO, Pageable pageable);

    /**
     * 카테고리별 상품 검색
     * @param categoryId 카테고리 ID
     * @param searchDTO 검색 조건
     * @param pageable 페이징 정보
     * @return 검색 결과
     */
    Page<ProductDTO> searchProductsByCategory(Long categoryId, ProductSearchDTO searchDTO, Pageable pageable);

    /**
     * 브랜드별 상품 검색
     * @param brand 브랜드명
     * @param searchDTO 검색 조건
     * @param pageable 페이징 정보
     * @return 검색 결과
     */
    Page<ProductDTO> searchProductsByBrand(String brand, ProductSearchDTO searchDTO, Pageable pageable);

    /**
     * 연관 상품 검색
     * @param productId 상품 ID
     * @param limit 조회 개수
     * @return 연관 상품 목록
     */
    java.util.List<ProductDTO> getRelatedProducts(Long productId, int limit);

    /**
     * 인기 검색어 목록 조회
     * @param limit 조회 개수
     * @return 인기 검색어 목록
     */
    java.util.List<String> getPopularSearchKeywords(int limit);
}