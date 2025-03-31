package com.peakmeshop.api.dto;

import java.time.LocalDateTime;

public record ShippingDTO(
        Long id,
        Long orderId,
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
    // 생성자 오버로딩 - 새 배송 정보 생성 시 사용
    public ShippingDTO(Long orderId, String carrier, String shippingAddress,
                       String recipientName, String recipientPhone, LocalDateTime estimatedDeliveryDate) {
        this(null, orderId, null, carrier, "PREPARING", shippingAddress, recipientName,
                recipientPhone, null, estimatedDeliveryDate, null, null, null);
    }

    // 생성자 오버로딩 - 배송 상태 업데이트 시 사용
    public ShippingDTO(Long id, String status) {
        this(id, null, null, null, status, null, null, null, null, null, null, null, null);
    }

    // 생성자 오버로딩 - 운송장 정보 업데이트 시 사용
    public ShippingDTO(Long id, String carrier, String trackingNumber, String status) {
        this(id, null, trackingNumber, carrier, status, null, null, null, null, null, null, null, null);
    }
}