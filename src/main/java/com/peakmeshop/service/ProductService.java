package com.peakmeshop.service;

import java.util.List;
import java.util.Optional;

import com.peakmeshop.dto.ProductDTO;

/**
 * 상품 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface ProductService {

    /**
     * 모든 상품 목록을 조회합니다.
     * @return 상품 목록
     */
    List<ProductDTO> findAllProducts();

    /**
     * 카테고리별 상품 목록을 조회합니다.
     * @param categoryId 카테고리 ID
     * @return 해당 카테고리의 상품 목록
     */
    List<ProductDTO> findProductsByCategory(Long categoryId);

    /**
     * ID로 상품을 조회합니다.
     * @param id 상품 ID
     * @return 상품 정보
     */
    Optional<ProductDTO> findProductById(Long id);

    /**
     * 상품명으로 상품을 검색합니다.
     * @param keyword 검색어
     * @return 검색 결과 상품 목록
     */
    List<ProductDTO> searchProductsByName(String keyword);

    /**
     * 새 상품을 저장합니다.
     * @param productDTO 저장할 상품 정보
     * @return 저장된 상품 정보
     */
    ProductDTO saveProduct(ProductDTO productDTO);

    /**
     * 상품 정보를 업데이트합니다.
     * @param id 업데이트할 상품 ID
     * @param productDTO 업데이트할 상품 정보
     * @return 업데이트된 상품 정보
     */
    ProductDTO updateProduct(Long id, ProductDTO productDTO);

    /**
     * 상품을 삭제합니다.
     * @param id 삭제할 상품 ID
     */
    void deleteProduct(Long id);

    /**
     * 상품 재고를 업데이트합니다.
     * @param id 상품 ID
     * @param quantity 변경할 수량 (감소는 음수)
     * @return 업데이트된 상품 정보
     */
    ProductDTO updateProductStock(Long id, Integer quantity);
}