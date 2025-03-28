package com.peakmeshop.service;

import java.util.List;

import com.peakmeshop.dto.ProductDTO;

public interface RecommendationService {

    /**
     * 회원 기반 상품 추천
     * @param memberId 회원 ID
     * @param limit 조회 개수
     * @return 추천 상품 목록
     */
    List<ProductDTO> getPersonalizedRecommendations(Long memberId, int limit);

    /**
     * 상품 기반 추천 (함께 구매한 상품)
     * @param productId 상품 ID
     * @param limit 조회 개수
     * @return 추천 상품 목록
     */
    List<ProductDTO> getFrequentlyBoughtTogether(Long productId, int limit);

    /**
     * 인기 상품 추천
     * @param categoryId 카테고리 ID (선택적)
     * @param limit 조회 개수
     * @return 인기 상품 목록
     */
    List<ProductDTO> getPopularProducts(Long categoryId, int limit);

    /**
     * 신규 상품 추천
     * @param categoryId 카테고리 ID (선택적)
     * @param limit 조회 개수
     * @return 신규 상품 목록
     */
    List<ProductDTO> getNewArrivals(Long categoryId, int limit);

    /**
     * 할인 상품 추천
     * @param limit 조회 개수
     * @return 할인 상품 목록
     */
    List<ProductDTO> getDiscountedProducts(int limit);

    /**
     * 조회 기록 기반 추천
     * @param memberId 회원 ID
     * @param limit 조회 개수
     * @return 추천 상품 목록
     */
    List<ProductDTO> getRecentlyViewedRecommendations(Long memberId, int limit);
}