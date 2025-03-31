package com.peakmeshop.api.dto;

import java.time.LocalDateTime;

/**
 * 주문 상태 정보를 전송하기 위한 DTO
 */
public record OrderStatusDTO(
        Long id,
        Long orderId,
        String orderNumber,
        String status, // PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED, REFUNDED
        String description,
        String trackingNumber,
        String trackingUrl,
        String carrierName,
        String updatedBy,
        LocalDateTime createdAt
) {}