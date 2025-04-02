package com.peakmeshop.domain.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.ProductDTO;

public interface ProductService {

    // 모든 상품 조회 (기존)
    Page<ProductDTO> getAllProducts(Pageable pageable);

    // 상품 ID로 조회 (기존)
    ProductDTO getProductById(Long id);

    // 상품 생성 (기존)
    ProductDTO createProduct(ProductDTO productDTO);

    // 상품 수정 (기존)
    ProductDTO updateProduct(Long id, ProductDTO productDTO);

    // 상품 삭제
    void deleteProduct(Long id);

    // 상품 활성화/비활성화
    ProductDTO toggleProductStatus(Long id);

    // 상품 재고 업데이트 (컨트롤러에서 사용)
    ProductDTO updateProductStock(Long id, int stock);

    // 상품 상태 업데이트 (컨트롤러에서 사용)
    ProductDTO updateProductStatus(Long id, String status);

    // 카테고리별 상품 조회
    Page<ProductDTO> getProductsByCategory(Long categoryId, Pageable pageable);

    // 브랜드별 상품 조회
    Page<ProductDTO> getProductsByBrand(String brand, Pageable pageable);

    // 상품 검색
    Page<ProductDTO> searchProducts(String keyword, Pageable pageable);

    // 추천 상품 조회
    Page<ProductDTO> getFeaturedProducts(Pageable pageable);

    // 신상품 조회
    Page<ProductDTO> getNewArrivals(Pageable pageable);

    // 베스트셀러 조회
    Page<ProductDTO> getBestSellers(Pageable pageable);

    // 할인 상품 조회
    Page<ProductDTO> getDiscountedProducts(Pageable pageable);

    // 상품 랭킹 업데이트
    void updateProductRankings();
}