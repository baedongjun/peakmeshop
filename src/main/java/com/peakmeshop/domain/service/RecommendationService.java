package com.peakmeshop.domain.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.ProductDTO;
import com.peakmeshop.api.dto.RecommendationDTO;

public interface RecommendationService {

    List<ProductDTO> getPersonalizedRecommendations(Long memberId, int limit);

    List<ProductDTO> getSimilarProducts(Long productId, int limit);

    List<ProductDTO> getFrequentlyBoughtTogether(Long productId, int limit);

    List<ProductDTO> getPopularProducts(int limit);

    List<ProductDTO> getNewArrivals(int limit);

    List<ProductDTO> getBestSellers(int limit);

    List<ProductDTO> getOnSaleProducts(int limit);

    List<ProductDTO> getRecentlyViewedProducts(Long memberId, int limit);

    void recordProductView(Long memberId, Long productId);

    void recordProductPurchase(Long memberId, Long productId);

    void generateRecommendations();

    Page<RecommendationDTO> getMemberRecommendations(Long memberId, Pageable pageable);

    void clearMemberRecommendations(Long memberId);
}