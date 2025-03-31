package com.peakmeshop.api.dto;

import com.peakmeshop.api.dto.ProductDTO;

public record ProductComparisonItemDTO(
        Long id,
        Long comparisonId,
        ProductDTO product,
        int position,
        String notes
) {}