package com.peakmeshop.api.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class RefundItemDTO {
    private Long id;
    private Long refundId;
    private Long orderItemId;
    private Long productId;
    private Long productVariantId;
    private String productName;
    private String variantName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private String reason;
    private String imageUrl;
}