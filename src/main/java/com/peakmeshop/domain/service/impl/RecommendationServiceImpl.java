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

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductViewRepository productViewRepository;
    private final ProductService productService;

    public RecommendationServiceImpl(
            ProductRepository productRepository,
            OrderItemRepository orderItemRepository,
            ProductViewRepository productViewRepository,
            ProductService productService) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.productViewRepository = productViewRepository;
        this.productService = productService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getPersonalizedRecommendations(Long memberId, int limit) {
        // 실제 구현에서는 회원의 구매 이력, 조회 이력 등을 분석하여 개인화된 추천 상품을 찾는 로직 구현
        // 여기서는 간단히 최근 본 상품과 유사한 상품을 추천
        List<Long> recentlyViewedProductIds = productViewRepository.findRecentlyViewedProductIds(memberId, 5);

        List<ProductDTO> recommendedProducts = new ArrayList<>();

        if (!recentlyViewedProductIds.isEmpty()) {
            // 최근 본 상품과 같은 카테고리의 상품 추천
            for (Long productId : recentlyViewedProductIds) {
                Product product = productRepository.findById(productId).orElse(null);
                if (product != null && product.getCategory() != null) {
                    List<Product> similarProducts = productRepository.findByCategoryIdAndIdNot(
                            product.getCategory().getId(), productId, PageRequest.of(0, 2));

                    for (Product similarProduct : similarProducts) {
                        // 중복 제거
                        if (recommendedProducts.stream().noneMatch(p -> p.getId().equals(similarProduct.getId()))) {
                            recommendedProducts.add(convertToDTO(similarProduct));
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

        // 추천 상품이 부족한 경우 인기 상품으로 채움
        if (recommendedProducts.size() < limit) {
            List<Product> popularProducts = productRepository.findAllByOrderBySalesCountDesc(
                    PageRequest.of(0, limit - recommendedProducts.size()));

            for (Product product : popularProducts) {
                // 중복 제거
                if (recommendedProducts.stream().noneMatch(p -> p.getId().equals(product.getId()))) {
                    recommendedProducts.add(convertToDTO(product));
                }
            }
        }

        return recommendedProducts;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getSimilarProducts(Long productId, int limit) {
        // 실제 구현에서는 상품 속성, 카테고리 등을 기반으로 유사 상품을 찾는 로직 구현
        // 여기서는 간단히 같은 카테고리의 상품을 추천
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));

        List<Product> similarProducts = new ArrayList<>();
        if (product.getCategory() != null) {
            similarProducts = productRepository.findByCategoryIdAndIdNot(
                    product.getCategory().getId(), productId, PageRequest.of(0, limit));
        }

        return similarProducts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getFrequentlyBoughtTogether(Long productId, int limit) {
        // 실제 구현에서는 주문 데이터를 분석하여 함께 구매되는 상품을 찾는 로직 구현
        // 여기서는 간단히 같은 주문에 포함된 상품을 추천
        List<Long> frequentlyBoughtProductIds = orderItemRepository.findFrequentlyBoughtTogetherProductIds(productId, limit);

        List<ProductDTO> productDTOs = new ArrayList<>();
        if (!frequentlyBoughtProductIds.isEmpty()) {
            List<Product> products = productRepository.findAllById(frequentlyBoughtProductIds);
            productDTOs = products.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }

        return productDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getPopularProducts(int limit) {
        // 전체 인기 상품
        List<Product> popularProducts = productRepository.findAllByOrderBySalesCountDesc(PageRequest.of(0, limit));

        return popularProducts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getNewArrivals(int limit) {
        // 신상품 (최근 등록된 상품)
        List<Product> newProducts = productRepository.findByIsActiveTrueOrderByCreatedAtDesc(PageRequest.of(0, limit)).getContent();

        return newProducts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getBestSellers(int limit) {
        // 베스트셀러 (판매량 기준)
        List<Product> bestSellers = productRepository.findByIsActiveTrueOrderBySalesCountDesc(PageRequest.of(0, limit)).getContent();

        return bestSellers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getOnSaleProducts(int limit) {
        // 할인 상품
        List<Product> onSaleProducts = productRepository.findByIsActiveTrueAndSalePriceIsNotNullOrderBySalePriceAsc(PageRequest.of(0, limit)).getContent();

        return onSaleProducts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getRecentlyViewedProducts(Long memberId, int limit) {
        // 최근 본 상품
        List<Long> recentlyViewedProductIds = productViewRepository.findRecentlyViewedProductIds(memberId, limit);

        List<ProductDTO> recentlyViewedProducts = new ArrayList<>();
        if (!recentlyViewedProductIds.isEmpty()) {
            List<Product> products = productRepository.findAllById(recentlyViewedProductIds);
            recentlyViewedProducts = products.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }

        return recentlyViewedProducts;
    }

    @Override
    @Transactional
    public void recordProductView(Long memberId, Long productId) {
        // 상품 조회 이벤트 기록 로직 구현
        if (memberId != null) {
            productViewRepository.saveProductView(productId, memberId);
        }
    }

    @Override
    @Transactional
    public void recordProductPurchase(Long memberId, Long productId) {
        // 상품 구매 이벤트 기록 로직 구현
        // 구매 이력 업데이트
    }

    @Override
    @Transactional
    public void generateRecommendations() {
        // 추천 모델 생성 로직 구현
        // 실제 구현에서는 머신러닝 모델 학습 등의 작업 수행
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RecommendationDTO> getMemberRecommendations(Long memberId, Pageable pageable) {
        // 회원별 추천 정보 조회 로직 구현
        // 여기서는 간단히 빈 페이지 반환
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    @Override
    @Transactional
    public void clearMemberRecommendations(Long memberId) {
        // 회원의 추천 정보 초기화 로직 구현
        // 예: 회원 탈퇴 시 관련 데이터 삭제
    }

    // 엔티티를 DTO로 변환
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setCode(product.getCode());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setSalePrice(product.getSalePrice());
        dto.setBrand(product.getBrand());
        dto.setCategoryId(product.getCategory().getId());
        dto.setSupplier(product.getSupplier());
        dto.setMainImage(product.getMainImage());
        dto.setStock(product.getStock());
        dto.setStockAlert(product.getStockAlert());
        dto.setMaxPurchaseQuantity(product.getMaxPurchaseQuantity());
        dto.setShortDescription(product.getShortDescription());
        dto.setStatus(product.getStatus());
        dto.setIsActive(product.getIsActive());
        dto.setIsFeatured(product.getIsFeatured());
        dto.setAverageRating(product.getAverageRating());
        dto.setReviewCount(product.getReviewCount());
        dto.setSalesCount(product.getSalesCount());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }
}