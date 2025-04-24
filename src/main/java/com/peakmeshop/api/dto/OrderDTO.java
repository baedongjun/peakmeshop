package com.peakmeshop.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.peakmeshop.domain.enums.PaymentMethod;
import com.peakmeshop.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;
    private String orderNumber;
    private Long memberId;
    private Long paymentId;
    private String memberName;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private BigDecimal discount;
    private BigDecimal deliveryFee;
    private Double finalPrice;
    private String recipientName;
    private String recipientTel;
    private String recipientAddress;
    private String recipientDetailAddress;
    private String recipientMessage;
    private PaymentMethod paymentMethod;
    private String trackingNumber;
    private String shippingCompany;
    private String cancelReason;
    private String refundReason;
    private LocalDateTime paidAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime refundedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private List<OrderItemDTO> items;

    private Long shippingId;
    private String shippingAddress;
    private String billingAddress;

    private String paymentMethodString;
    private String paymentStatus;
    private String shippingStatus;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private BigDecimal finalAmount;
    private String statusString;
}