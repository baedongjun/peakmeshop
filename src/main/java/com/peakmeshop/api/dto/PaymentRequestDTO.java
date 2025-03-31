package com.peakmeshop.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 결제 요청 정보를 전송하기 위한 DTO
 */
public record PaymentRequestDTO(
        @NotNull(message = "주문 ID는 필수입니다")
        Long orderId,

        @NotBlank(message = "주문 번호는 필수입니다")
        String orderNumber,

        @NotNull(message = "결제 금액은 필수입니다")
        @Positive(message = "결제 금액은 양수여야 합니다")
        BigDecimal amount,

        @NotBlank(message = "통화는 필수입니다")
        String currency,

        @NotBlank(message = "결제 방법은 필수입니다")
        String paymentMethod,

        String cardNumber,

        String cardExpiryMonth,

        String cardExpiryYear,

        String cardCvc,

        String cardOwnerName,

        String returnUrl,

        String cancelUrl
) {}