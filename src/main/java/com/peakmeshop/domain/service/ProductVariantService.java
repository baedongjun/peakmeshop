package com.peakmeshop.domain.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.ProductVariantDTO;

public interface ProductVariantService {

    Page<ProductVariantDTO> getVariantsByProductId(Long productId, Pageable pageable);

    Optional<ProductVariantDTO> getVariantById(Long id);

    Optional<ProductVariantDTO> getVariantBySku(String sku);

    ProductVariantDTO createVariant(ProductVariantDTO variantDTO);

    ProductVariantDTO updateVariant(Long id, ProductVariantDTO variantDTO);

    boolean deleteVariant(Long id);

    List<ProductVariantDTO> createVariantsFromCombinations(
            Long productId, Map<String, List<String>> attributeCombinations);

    void updateVariantQuantity(Long id, int quantity);

    void updateVariantPrice(Long id, BigDecimal price);

    void enableVariant(Long id);

    void disableVariant(Long id);

    List<Map<String, String>> getPossibleCombinations(Long productId);

    boolean isVariantInStock(Long id);

    int getVariantQuantity(Long id);
}