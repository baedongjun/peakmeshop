package com.peakmeshop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 주문 요약 정보를 전송하기 위한 DTO (목록 조회용)
 */
public record OrderSummaryDTO(
        Long id,
        String orderNumber,
        BigDecimal totalAmount,
        String status,
        LocalDateTime orderDate,
        String recipientName,
        int itemCount
) {}