package com.peakmeshop.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequestDTO {
    @NotNull(message = "주문 ID는 필수입니다.")
    private Long orderId;

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;

    @NotNull(message = "환불 금액은 필수입니다.")
    @Positive(message = "환불 금액은 0보다 커야 합니다.")
    private BigDecimal amount;

    @NotBlank(message = "환불 사유는 필수입니다.")
    private String reason;
}