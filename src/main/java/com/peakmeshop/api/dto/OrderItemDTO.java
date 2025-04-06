package com.peakmeshop.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO {

    private Long id;
    private Long orderId;
    private Long productId;
    private String name;
    private BigDecimal price;
    private BigDecimal cost;
    private Integer quantity;
    private Map<String, String> options;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}