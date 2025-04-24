package com.peakmeshop.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundItemDTO {
    private Long id;
    private Long refundId;
    private Long orderId;
    private Long orderItemId;
    private Long productId;
    private Long productOptionId;
    private String productName;
    private String optionName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}