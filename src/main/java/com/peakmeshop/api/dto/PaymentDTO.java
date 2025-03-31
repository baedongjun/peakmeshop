package com.peakmeshop.api.dto;

import java.time.LocalDateTime;

public record PaymentDTO(
        Long id,
        Long orderId,
        String transactionId,
        String paymentMethod,
        double amount,
        String status,
        LocalDateTime paymentDate,
        String refundReason,
        LocalDateTime refundDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    // 생성자 오버로딩 - 새 결제 생성 시 사용
    public PaymentDTO(Long orderId, String paymentMethod, double amount) {
        this(null, orderId, null, paymentMethod, amount, null, null, null, null, null, null);
    }

    // 생성자 오버로딩 - 환불 처리 시 사용
    public PaymentDTO(Long id, String refundReason) {
        this(id, null, null, null, 0, null, null, refundReason, null, null, null);
    }
}