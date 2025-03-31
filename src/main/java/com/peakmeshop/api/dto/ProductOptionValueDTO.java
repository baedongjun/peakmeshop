package com.peakmeshop.api.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductOptionValueDTO {

    private Long id;
    private Long optionId;
    private String value;
    private BigDecimal priceAdjustment;
    private Integer stockQuantity;
    private String sku;
    private Boolean active;
}