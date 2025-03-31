package com.peakmeshop.domain.service.impl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.ProductComparisonDTO;
import com.peakmeshop.api.dto.ProductDTO;
import com.peakmeshop.domain.entity.Member;
import com.peakmeshop.domain.entity.Product;
import com.peakmeshop.domain.entity.ProductComparison;
import com.peakmeshop.common.exception.ResourceNotFoundException;
import com.peakmeshop.domain.repository.MemberRepository;
import com.peakmeshop.domain.repository.ProductComparisonRepository;
import com.peakmeshop.domain.repository.ProductRepository;
import com.peakmeshop.domain.service.ProductComparisonService;
import com.peakmeshop.domain.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductComparisonServiceImpl implements ProductComparisonService {

    private final ProductComparisonRepository comparisonRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final ProductService productService;

    private static final int MAX_COMPARISON_PRODUCTS = 4;
    private static final int MAX_RECENT_PRODUCTS = 10;

    @Override
    @Transactional
    public void addProductToComparison(Long memberId, Long productId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다."));

        Optional<ProductComparison> comparisonOpt = comparisonRepository.findByMemberIdAndIsRecentFalse(memberId);

        ProductComparison comparison;
        if (comparisonOpt.isPresent()) {
            comparison = comparisonOpt.get();
        } else {
            comparison = ProductComparison.builder()
                    .member(member)
                    .isRecent(false)
                    .createdAt(LocalDateTime.now())
                    .products(new ArrayList<>())
                    .build();
        }

        List<Long> products = comparison.getProducts();

        // 이미 비교 목록에 있는 상품인 경우 추가하지 않음
        if (products.contains(productId)) {
            return;
        }

        // 비교 목록이 최대 개수에 도달한 경우 가장 오래된 상품 제거
        if (products.size() >= MAX_COMPARISON_PRODUCTS) {
            products.remove(0);
        }

        products.add(productId);
        comparison.setUpdatedAt(LocalDateTime.now());

        comparisonRepository.save(comparison);

        // 최근 비교한 상품 목록에도 추가
        addToRecentlyCompared(memberId, productId);
    }

    @Override
    @Transactional
    public void removeProductFromComparison(Long memberId, Long productId) {
        Optional<ProductComparison> comparisonOpt = comparisonRepository.findByMemberIdAndIsRecentFalse(memberId);

        if (comparisonOpt.isEmpty()) {
            return;
        }

        ProductComparison comparison = comparisonOpt.get();
        List<Long> products = comparison.getProducts();

        if (products.remove(productId)) {
            comparison.setUpdatedAt(LocalDateTime.now());
            comparisonRepository.save(comparison);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ProductComparisonDTO getComparisonList(Long memberId) {
        Optional<ProductComparison> comparisonOpt = comparisonRepository.findByMemberIdAndIsRecentFalse(memberId);

        if (comparisonOpt.isEmpty()) {
            return ProductComparisonDTO.builder()
                    .memberId(memberId)
                    .products(new ArrayList<>())
                    .build();
        }

        ProductComparison comparison = comparisonOpt.get();
        List<ProductDTO> productDTOs = comparison.getProducts().stream()
                .map(productId -> productService.getProductById(productId))
                .collect(Collectors.toList());

        return ProductComparisonDTO.builder()
                .id(comparison.getId())
                .memberId(memberId)
                .products(productDTOs)
                .createdAt(comparison.getCreatedAt())
                .updatedAt(comparison.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public void addToRecentlyCompared(Long memberId, Long productId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다."));

        Optional<ProductComparison> recentOpt = comparisonRepository.findByMemberIdAndIsRecentTrue(memberId);

        ProductComparison recentComparison;
        if (recentOpt.isPresent()) {
            recentComparison = recentOpt.get();
        } else {
            recentComparison = ProductComparison.builder()
                    .member(member)
                    .isRecent(true)
                    .createdAt(LocalDateTime.now())
                    .products(new ArrayList<>())
                    .build();
        }

        List<Long> products = recentComparison.getProducts();

        // 이미 최근 비교 목록에 있는 상품인 경우 제거 후 다시 추가 (최신 순서 유지)
        products.remove(productId);

        // 최근 비교 목록이 최대 개수에 도달한 경우 가장 오래된 상품 제거
        if (products.size() >= MAX_RECENT_PRODUCTS) {
            products.remove(0);
        }

        products.add(productId);
        recentComparison.setUpdatedAt(LocalDateTime.now());

        comparisonRepository.save(recentComparison);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getRecentlyComparedProducts(Long memberId) {
        Optional<ProductComparison> recentOpt = comparisonRepository.findByMemberIdAndIsRecentTrue(memberId);

        if (recentOpt.isEmpty()) {
            return new ArrayList<>();
        }

        ProductComparison recentComparison = recentOpt.get();

        // 최신 순서로 정렬 (가장 최근에 추가된 상품이 마지막에 있으므로 역순으로 반환)
        List<Long> productIds = new ArrayList<>(recentComparison.getProducts());
        Collections.reverse(productIds);

        return productIds.stream()
                .map(productId -> productService.getProductById(productId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getRecentlyComparedProducts(Long memberId, int limit) {
        List<ProductDTO> recentProducts = getRecentlyComparedProducts(memberId);

        if (recentProducts.size() <= limit) {
            return recentProducts;
        }

        return recentProducts.subList(0, limit);
    }

    @Override
    @Transactional
    public void clearRecentlyCompared(Long memberId) {
        Optional<ProductComparison> recentOpt = comparisonRepository.findByMemberIdAndIsRecentTrue(memberId);

        if (recentOpt.isPresent()) {
            ProductComparison recentComparison = recentOpt.get();
            recentComparison.getProducts().clear();
            recentComparison.setUpdatedAt(LocalDateTime.now());
            comparisonRepository.save(recentComparison);
        }
    }
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> compareProducts(List<Long> productIds) {
        Map<String, Object> result = new HashMap<>();

        List<ProductDTO> products = productIds.stream()
                .map(productId -> productService.getProductById(productId))
                .collect(Collectors.toList());

        result.put("products", products);

        // 상품 속성 비교 데이터 생성
        Map<String, List<Object>> comparisonData = new HashMap<>();

        // 기본 속성 비교
        comparisonData.put("name", products.stream().map(ProductDTO::getName).collect(Collectors.toList()));
        comparisonData.put("price", products.stream().map(ProductDTO::getPrice).collect(Collectors.toList()));
        comparisonData.put("discountedPrice", products.stream().map(ProductDTO::getDiscountedPrice).collect(Collectors.toList()));
        comparisonData.put("brand", products.stream().map(ProductDTO::getBrand).collect(Collectors.toList()));
        comparisonData.put("category", products.stream().map(ProductDTO::getCategoryName).collect(Collectors.toList()));
        comparisonData.put("rating", products.stream().map(ProductDTO::getAverageRating).collect(Collectors.toList()));
        comparisonData.put("reviewCount", products.stream().map(ProductDTO::getReviewCount).collect(Collectors.toList()));

        // 상품 속성 비교
        for (int i = 0; i < products.size(); i++) {
            ProductDTO product = products.get(i);
            Map<String, Object> attributes = product.getAttributes();

            if (attributes != null) {
                for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    if (!comparisonData.containsKey(key)) {
                        comparisonData.put(key, new ArrayList<>());
                        // 이전 상품들에 대한 빈 값 채우기
                        for (int j = 0; j < i; j++) {
                            comparisonData.get(key).add(null);
                        }
                    }
                    comparisonData.get(key).add(value);
                }
            }
        }

        // 속성 데이터 길이 맞추기
        comparisonData.forEach((key, values) -> {
            while (values.size() < products.size()) {
                values.add(null);
            }
        });

        result.put("comparisonData", comparisonData);

        return result;
    }

    @Override
    @Transactional
    public void addProductsToComparison(Long memberId, List<Long> productIds) {
        for (Long productId : productIds) {
            addProductToComparison(memberId, productId);
        }
    }

}