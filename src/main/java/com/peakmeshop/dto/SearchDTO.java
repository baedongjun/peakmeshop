package com.peakmeshop.dto;

import java.util.List;
import java.util.Map;

public record SearchDTO(
        String keyword,
        Long categoryId,
        List<Long> brandIds,
        Double minPrice,
        Double maxPrice,
        List<Map<String, String>> attributes,
        Boolean inStock,
        Boolean onSale,
        String sortBy,
        String sortDirection
) {}