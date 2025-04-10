package com.peakmeshop.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.peakmeshop.domain.enums.OrderStatus;
import com.peakmeshop.domain.enums.RefundStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundDTO {
    private Long id;
    private String orderNumber;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private Double totalAmount;
    private LocalDateTime refundedAt;
    private Long orderId;
    private String refundNumber;
    private BigDecimal amount;
    private BigDecimal shippingAmount;
    private RefundStatus status;
    private String adminNotes;
    private String reason;
    private String transactionId;
    private OrderStatus orderStatus;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private LocalDateTime completedAt;
    private LocalDateTime rejectedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<RefundItemDTO> items;
}