package com.peakmeshop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 주문 정보를 전송하기 위한 DTO
 */
public record OrderDTO(
        Long id,

        String orderNumber,

        @NotNull(message = "회원 ID는 필수입니다")
        Long memberId,

        BigDecimal totalAmount,

        String status,

        String paymentMethod,

        @NotBlank(message = "배송 주소는 필수입니다")
        String shippingAddress,

        String shippingDetailAddress,

        @NotBlank(message = "우편번호는 필수입니다")
        String shippingZipCode,

        @NotBlank(message = "수령인 이름은 필수입니다")
        String recipientName,

        @NotBlank(message = "수령인 연락처는 필수입니다")
        String recipientPhone,

        LocalDateTime orderDate,

        LocalDateTime paymentDate,

        LocalDateTime shippingDate,

        LocalDateTime completionDate,

        @NotEmpty(message = "주문 상품은 최소 1개 이상이어야 합니다")
        List<OrderItemDTO> orderItems
) {
    // 빌더 패턴을 위한 정적 메서드
    public static Builder builder() {
        return new Builder();
    }

    // 빌더 클래스
    public static class Builder {
        private Long id;
        private String orderNumber;
        private Long memberId;
        private BigDecimal totalAmount;
        private String status;
        private String paymentMethod;
        private String shippingAddress;
        private String shippingDetailAddress;
        private String shippingZipCode;
        private String recipientName;
        private String recipientPhone;
        private LocalDateTime orderDate;
        private LocalDateTime paymentDate;
        private LocalDateTime shippingDate;
        private LocalDateTime completionDate;
        private List<OrderItemDTO> orderItems;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder orderNumber(String orderNumber) { this.orderNumber = orderNumber; return this; }
        public Builder memberId(Long memberId) { this.memberId = memberId; return this; }
        public Builder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder paymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public Builder shippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; return this; }
        public Builder shippingDetailAddress(String shippingDetailAddress) { this.shippingDetailAddress = shippingDetailAddress; return this; }
        public Builder shippingZipCode(String shippingZipCode) { this.shippingZipCode = shippingZipCode; return this; }
        public Builder recipientName(String recipientName) { this.recipientName = recipientName; return this; }
        public Builder recipientPhone(String recipientPhone) { this.recipientPhone = recipientPhone; return this; }
        public Builder orderDate(LocalDateTime orderDate) { this.orderDate = orderDate; return this; }
        public Builder paymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; return this; }
        public Builder shippingDate(LocalDateTime shippingDate) { this.shippingDate = shippingDate; return this; }
        public Builder completionDate(LocalDateTime completionDate) { this.completionDate = completionDate; return this; }
        public Builder orderItems(List<OrderItemDTO> orderItems) { this.orderItems = orderItems; return this; }

        public OrderDTO build() {
            return new OrderDTO(id, orderNumber, memberId, totalAmount, status, paymentMethod,
                    shippingAddress, shippingDetailAddress, shippingZipCode, recipientName, recipientPhone,
                    orderDate, paymentDate, shippingDate, completionDate, orderItems);
        }
    }
}