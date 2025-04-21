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
public class WishlistItemDTO {

    private Long id;
    private Long wishlistId;
    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private BigDecimal productSalePrice;
    private String productMainImage;
    private LocalDateTime createdAt;
}