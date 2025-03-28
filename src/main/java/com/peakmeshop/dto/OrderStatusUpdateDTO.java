package com.peakmeshop.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 주문 상태 업데이트를 위한 DTO
 */
public record OrderStatusUpdateDTO(
        @NotBlank(message = "주문 상태는 필수입니다")
        String status,

        String reason  // 취소 사유 등
) {}