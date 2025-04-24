package com.peakmeshop.domain.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.ProductDTO;
import com.peakmeshop.api.dto.RecommendationDTO;
import com.peakmeshop.domain.entity.Product;
import com.peakmeshop.domain.repository.OrderItemRepository;
import com.peakmeshop.domain.repository.ProductRepository;
import com.peakmeshop.domain.repository.ProductViewRepository;
import com.peakmeshop.domain.service.ProductService;
import com.peakmeshop.domain.service.RecommendationService;
import com.peakmeshop.api.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductViewRepository productViewRepository;
    private final ProductService productService;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getPersonalizedRecommendations(Long memberId, int limit) {
        List<Long> recentlyViewedProductIds = productViewRepository.findRecentlyViewedProductIds(memberId, 5);
        List<ProductDTO> recommendedProducts = new ArrayList<>();

        if (!recentlyViewedProductIds.isEmpty()) {
            for (Long productId : recentlyViewedProductIds) {
                Product product = productRepository.findById(productId).orElse(null);
                if (product != null && product.getCategory() != null) {
                    List<Product> similarProducts = productRepository.findByCategoryIdAndIdNot(
                            product.getCategory().getId(), productId, PageRequest.of(0, 2));

                    for (Product similarProduct : similarProducts) {
                        if (recommendedProducts.stream().noneMatch(p -> p.getId().equals(similarProduct.getId()))) {
                            recommendedProducts.add(productMapper.toDTO(similarProduct));
                            if (recommendedProducts.size() >= limit) {
                                break;
                            }
                        }
                    }

                    if (recommendedProducts.size() >= limit) {
                        break;
                    }
                }
            }
        }

        if (recommendedProducts.size() < limit) {
            List<Product> popularProducts = productRepository.findAllByOrderBySalesCountDesc(
                    PageRequest.of(0, limit - recommendedProducts.size()));

            for (Product product : popularProducts) {
                if (recommendedProducts.stream().noneMatch(p -> p.getId().equals(product.getId()))) {
                    recommendedProducts.add(productMapper.toDTO(product));
                }
            }
        }

        return recommendedProducts;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getPopularProducts(int limit) {
        return productRepository.findByIsActiveTrueOrderBySalesCountDesc(PageRequest.of(0, limit))
                .stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getNewArrivals(int limit) {
        return productRepository.findByIsActiveTrueOrderByCreatedAtDesc(PageRequest.of(0, limit))
                .stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getBestSellers(int limit) {
        return productRepository.findByIsActiveTrueOrderBySalesCountDesc(PageRequest.of(0, limit))
                .stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getFrequentlyBoughtTogether(Long productId, int limit) {
        List<Long> frequentlyBoughtProductIds = orderItemRepository.findFrequentlyBoughtTogetherProductIds(productId, limit);
        List<ProductDTO> productDTOs = new ArrayList<>();
        
        if (!frequentlyBoughtProductIds.isEmpty()) {
            List<Product> products = productRepository.findAllById(frequentlyBoughtProductIds);
            productDTOs = products.stream()
                    .map(productMapper::toDTO)
                    .collect(Collectors.toList());
        }

        return productDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getSimilarProducts(Long productId, int limit) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));

        List<Product> similarProducts = new ArrayList<>();
        if (product.getCategory() != null) {
            similarProducts = productRepository.findByCategoryIdAndIdNot(
                    product.getCategory().getId(), productId, PageRequest.of(0, limit));
        }

        return similarProducts.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getRecentlyViewedProducts(Long memberId, int limit) {
        List<Long> recentlyViewedProductIds = productViewRepository.findRecentlyViewedProductIds(memberId, limit);
        List<ProductDTO> recentlyViewedProducts = new ArrayList<>();
        
        if (!recentlyViewedProductIds.isEmpty()) {
            List<Product> products = productRepository.findAllById(recentlyViewedProductIds);
            recentlyViewedProducts = products.stream()
                    .map(productMapper::toDTO)
                    .collect(Collectors.toList());
        }

        return recentlyViewedProducts;
    }

    @Override
    @Transactional
    public void recordProductView(Long memberId, Long productId) {
        productViewRepository.saveProductView(productId, memberId);
    }

    @Override
    @Transactional
    public void recordProductPurchase(Long memberId, Long productId) {
        // 상품 구매 기록 저장 로직 구현
    }

    @Override
    @Transactional
    public void generateRecommendations() {
        // 추천 상품 생성 로직 구현
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RecommendationDTO> getMemberRecommendations(Long memberId, Pageable pageable) {
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    @Override
    @Transactional
    public void clearMemberRecommendations(Long memberId) {
        // 회원별 추천 상품 초기화 로직 구현
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getOnSaleProducts(int limit) {
        return productRepository.findByIsActiveTrueAndSalePriceIsNotNullOrderBySalePriceAsc(PageRequest.of(0, limit))
                .stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }
}