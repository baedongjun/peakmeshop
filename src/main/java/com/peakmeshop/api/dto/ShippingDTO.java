package com.peakmeshop.api.dto;

import java.time.LocalDateTime;

public record ShippingDTO(
        Long id,
        Long orderId,
        String orderNumber,
        String trackingNumber,
        String carrier,
        String status,
        String shippingAddress,
        String recipientName,
        String recipientPhone,
        LocalDateTime shippingDate,
        LocalDateTime estimatedDeliveryDate,
        LocalDateTime deliveryDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}