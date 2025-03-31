package com.peakmeshop.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantDTO {
    private Long id;
    private Long productId;
    private String sku;
    private String name;
    private BigDecimal price;
    private int quantity;
    private Map<String, String> attributes = new HashMap<>();
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}