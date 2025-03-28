package com.peakmeshop.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 통계 정보를 전송하기 위한 DTO
 */
public record StatisticsDTO(
        // 요약 통계
        int totalOrders,
        int totalMembers,
        int totalProducts,
        BigDecimal totalSales,

        // 기간별 매출
        Map<String, BigDecimal> salesByPeriod,

        // 카테고리별 매출
        List<CategorySalesDTO> salesByCategory,

        // 인기 상품
        List<ProductSummaryDTO> topSellingProducts,

        // 신규 회원
        int newMembersToday,
        int newMembersThisWeek,
        int newMembersThisMonth,

        // 주문 상태별 통계
        Map<String, Integer> ordersByStatus
) {}