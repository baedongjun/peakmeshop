package com.peakmeshop.api.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private Long orderId;
    private String paymentMethod;
    private String paymentStatus;
    private Double amount;
    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 생성자 오버로딩 - 새 결제 생성 시 사용
    public PaymentDTO(Long orderId, String paymentMethod, double amount) {
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
    }

    // 생성자 오버로딩 - 환불 처리 시 사용
    public PaymentDTO(Long id, String refundReason) {
        this.id = id;
    }
}