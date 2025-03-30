package com.peakmeshop.service;

import java.util.List;
import java.util.Map;

import com.peakmeshop.dto.ProductComparisonDTO;
import com.peakmeshop.dto.ProductDTO;

public interface ProductComparisonService {

    // 상품 비교 목록에 상품 추가
    void addProductToComparison(Long memberId, Long productId);

    // 상품 비교 목록에서 상품 제거
    void removeProductFromComparison(Long memberId, Long productId);

    // 상품 비교 목록 조회
    ProductComparisonDTO getComparisonList(Long memberId);

    // 최근 비교한 상품 목록 조회
    List<ProductDTO> getRecentlyComparedProducts(Long memberId);

    // 최근 비교한 상품 목록 조회 (제한 개수)
    List<ProductDTO> getRecentlyComparedProducts(Long memberId, int limit);

    // 최근 비교한 상품 목록에 상품 추가
    void addToRecentlyCompared(Long memberId, Long productId);

    // 최근 비교한 상품 목록 초기화
    void clearRecentlyCompared(Long memberId);

    // 상품 목록 비교 (컨트롤러에서 사용)
    Map<String, Object> compareProducts(List<Long> productIds);

    // 상품 목록 비교 (여러 상품 ID 추가)
    void addProductsToComparison(Long memberId, List<Long> productIds);
}