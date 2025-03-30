package com.peakmeshop.dto;

import java.math.BigDecimal;

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
    private String productName;
    private String productImage;
    private int quantity;
    private BigDecimal price;
    private BigDecimal discount;
    private BigDecimal totalPrice;
    private String options;
}