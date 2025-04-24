package com.peakmeshop.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private String name;
    private String value;
    private Integer sortOrder;
    private BigDecimal additionalPrice;
    private Integer stock;
    private String sku;
    private boolean enabled;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}