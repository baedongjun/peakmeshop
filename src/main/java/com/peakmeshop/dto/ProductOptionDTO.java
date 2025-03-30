package com.peakmeshop.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductOptionDTO {

    private Long id;
    private Long productId;
    private String name;
    private String value;
    private BigDecimal priceAdjustment;
    private Integer stockQuantity;
    private String sku;
    private Boolean active;

    @Builder.Default
    private List<ProductOptionValueDTO> values = new ArrayList<>();
}