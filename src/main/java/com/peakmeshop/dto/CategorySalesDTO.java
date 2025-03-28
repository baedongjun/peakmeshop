package com.peakmeshop.dto;

import java.math.BigDecimal;

/**
 * 카테고리별 매출 정보를 전송하기 위한 DTO
 */
public record CategorySalesDTO(
        Long categoryId,
        String categoryName,
        BigDecimal totalSales,
        int orderCount,
        double percentage
) {}