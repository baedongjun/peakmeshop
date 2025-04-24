package com.peakmeshop.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
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
    private String paymentMethod;
    private String paymentKey;
    private String orderId;
    private BigDecimal amount;
    private String status;
    private String description;
    private LocalDateTime paidAt;
    private LocalDateTime cancelledAt;
    private String failureReason;
    private Map<String, Object> additionalInfo;

    // 생성자 오버로딩 - 새 결제 생성 시 사용
    public PaymentDTO(Long orderId, String paymentMethod, double amount) {
        this.orderId = orderId.toString();
        this.paymentMethod = paymentMethod;
        this.amount = BigDecimal.valueOf(amount);
    }

    // 생성자 오버로딩 - 환불 처리 시 사용
    public PaymentDTO(Long id, String refundReason) {
        this.paymentKey = id.toString();
        this.failureReason = refundReason;
    }
}