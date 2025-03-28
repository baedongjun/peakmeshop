package com.peakmeshop.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 결제 요청을 위한 DTO
 */
public record PaymentRequestDTO(
        @NotBlank(message = "주문 번호는 필수입니다")
        String merchantUid,

        @NotNull(message = "결제 금액은 필수입니다")
        @Positive(message = "결제 금액은 양수여야 합니다")
        BigDecimal amount,

        @NotBlank(message = "결제 방법은 필수입니다")
        String paymentMethod,

        @NotBlank(message = "구매자 이름은 필수입니다")
        String buyerName,

        @NotBlank(message = "구매자 이메일은 필수입니다")
        String buyerEmail,

        @NotBlank(message = "구매자 연락처는 필수입니다")
        String buyerTel
) {}