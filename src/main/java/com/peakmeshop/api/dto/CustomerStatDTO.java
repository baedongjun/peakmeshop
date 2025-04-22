package com.peakmeshop.api.dto;

import java.time.LocalDateTime;

/**
 * 고객 통계 정보를 전송하기 위한 DTO
 */
public record CustomerStatDTO(
        Long memberId,
        String memberName,
        String memberEmail,
        int orderCount,
        double totalOrderAmount,
        double averageOrderValue,
        LocalDateTime firstOrderDate,
        LocalDateTime lastOrderDate
) {}