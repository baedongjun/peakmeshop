package com.peakmeshop.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 상품 검색 조건을 전송하기 위한 DTO
 */
public record ProductSearchDTO(
        String keyword,
        List<Long> categoryIds,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        List<String> brands,
        Boolean inStock,
        String sortBy,
        String sort
) {}