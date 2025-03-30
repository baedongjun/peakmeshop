package com.peakmeshop.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemOptionDTO {

    private Long id;
    private Long optionId;
    private String name;
    private String value;
    private BigDecimal additionalPrice;
}