package com.peakmeshop.api.dto;

import java.math.BigDecimal;
import java.util.List;

import com.peakmeshop.api.dto.OrderItemDTO;
import com.peakmeshop.domain.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {

    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal shippingCost;
    private BigDecimal tax;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private String shippingMethod;
    private Long shippingAddressId;
    private Long billingAddressId;
    private List<OrderItemDTO> items;
    private String couponCode;
    private String notes;
    private String recipientName;
    private String recipientTel;
    private String recipientAddress;
    private String recipientDetailAddress;
    private String recipientMessage;
}