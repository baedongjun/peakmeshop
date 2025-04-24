package com.peakmeshop.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 주문 요약 정보를 전송하기 위한 DTO (목록 조회용)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDTO {
    private Long id;
    private String orderNumber;
    private String memberName;
    private String status;
    private BigDecimal totalAmount;
    private Integer totalItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long totalOrders;
    private long monthlyOrders;
    private long dailyOrders;
    private double totalRevenue;
    private double monthlyRevenue;
    private double dailyRevenue;
    private double averageOrderValue;
    private long pendingOrders;
    private long processingOrders;
    private long completedOrders;
    private long cancelledOrders;
}