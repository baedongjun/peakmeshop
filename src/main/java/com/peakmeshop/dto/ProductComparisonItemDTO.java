package com.peakmeshop.dto;

public record ProductComparisonItemDTO(
        Long id,
        Long comparisonId,
        ProductDTO product,
        int position,
        String notes
) {}