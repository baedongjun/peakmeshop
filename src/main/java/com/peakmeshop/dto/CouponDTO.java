package com.peakmeshop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 쿠폰 정보를 전송하기 위한 DTO
 */
public record CouponDTO(
        Long id,

        @NotBlank(message = "쿠폰 코드는 필수입니다")
        String code,

        @NotBlank(message = "쿠폰명은 필수입니다")
        String name,

        String description,

        @NotNull(message = "할인 유형은 필수입니다")
        String discountType, // PERCENTAGE, FIXED_AMOUNT

        @NotNull(message = "할인 값은 필수입니다")
        @Positive(message = "할인 값은 양수여야 합니다")
        BigDecimal discountValue,

        @NotNull(message = "최소 주문 금액은 필수입니다")
        BigDecimal minimumOrderAmount,

        BigDecimal maximumDiscountAmount,

        @NotNull(message = "시작일은 필수입니다")
        LocalDateTime startDate,

        @NotNull(message = "종료일은 필수입니다")
        @Future(message = "종료일은 미래 날짜여야 합니다")
        LocalDateTime endDate,

        @NotNull(message = "사용 가능 횟수는 필수입니다")
        Integer usageLimit,

        Integer usedCount,

        Boolean isActive
) {}