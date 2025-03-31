package com.peakmeshop.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.peakmeshop.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    private Long id;
    private String orderNumber;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private OrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal shippingCost;
    private BigDecimal tax;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String paymentTransactionId;
    private String shippingMethod;
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
    private List<OrderItemDTO> items;

    private Long shippingAddressId;
    private Long billingAddressId;
    private com.peakmeshop.api.dto.AddressDTO shippingAddress;
    private AddressDTO billingAddress;

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public Long getShippingAddressId() {
        return shippingAddressId;
    }

    public Long getBillingAddressId() {
        return billingAddressId;
    }
}