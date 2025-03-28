package com.peakmeshop.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 장바구니 아이템 정보를 전송하기 위한 DTO
 */
public record CartItemDTO(
        Long id,

        @NotNull(message = "상품 ID는 필수입니다")
        Long productId,

        String productName,

        String productImage,

        @NotNull(message = "수량은 필수입니다")
        @Positive(message = "수량은 양수여야 합니다")
        Integer quantity,

        java.math.BigDecimal price,

        java.math.BigDecimal subtotal,

        Boolean isAvailable
) {}