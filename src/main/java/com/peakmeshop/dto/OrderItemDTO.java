package com.peakmeshop.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 주문 상품 정보를 전송하기 위한 DTO
 */
public record OrderItemDTO(
        Long id,

        @NotNull(message = "상품 ID는 필수입니다")
        Long productId,

        @NotBlank(message = "상품명은 필수입니다")
        String productName,

        @NotNull(message = "가격은 필수입니다")
        BigDecimal price,

        @NotNull(message = "수량은 필수입니다")
        @Positive(message = "수량은 양수여야 합니다")
        Integer quantity,

        BigDecimal subtotal
) {
    // 빌더 패턴을 위한 정적 메서드
    public static Builder builder() {
        return new Builder();
    }

    // 빌더 클래스
    public static class Builder {
        private Long id;
        private Long productId;
        private String productName;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal subtotal;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder productId(Long productId) { this.productId = productId; return this; }
        public Builder productName(String productName) { this.productName = productName; return this; }
        public Builder price(BigDecimal price) { this.price = price; return this; }
        public Builder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public Builder subtotal(BigDecimal subtotal) { this.subtotal = subtotal; return this; }

        public OrderItemDTO build() {
            return new OrderItemDTO(id, productId, productName, price, quantity, subtotal);
        }
    }
}