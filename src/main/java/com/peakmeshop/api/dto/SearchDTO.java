package com.peakmeshop.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class SearchDTO {
    private String keyword;
    private Long categoryId;
    private List<Long> brandIds;
    private Double minPrice;
    private Double maxPrice;
    private List<Map<String, String>> attributes;
    private Boolean inStock;
    private Boolean onSale;
    private String sortBy;
    private String sortDirection;
}