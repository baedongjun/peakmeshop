package com.peakmeshop.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.peakmeshop.domain.enums.OrderStatus;
import com.peakmeshop.domain.enums.RefundStatus;

import lombok.Data;

@Data
public class RefundDTO {
    private Long id;
    private Long orderId;
    private String orderNumber;
    private Long memberId;
    private String memberEmail;
    private String refundNumber;
    private RefundStatus status;
    private String reason;
    private String refundMethod;
    private BigDecimal amount;
    private BigDecimal shippingAmount;
    private BigDecimal totalAmount;
    private String transactionId;
    private List<RefundItemDTO> items;
    private Map<String, String> metadata;
    private String adminNotes;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private LocalDateTime completedAt;
    private LocalDateTime rejectedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private OrderStatus orderStatus;
}