package com.peakmeshop.dto;

import java.util.List;

/**
 * 검색 결과를 전송하기 위한 DTO
 */
public record SearchResultDTO(
        List<ProductDTO> products,
        long totalElements,
        int totalPages,
        int currentPage,
        List<FacetDTO> facets
) {}