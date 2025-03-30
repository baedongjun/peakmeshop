package com.peakmeshop.dto;

/**
 * 상품 통계 정보를 전송하기 위한 DTO
 */
public record ProductStatDTO(
        Long productId,
        String productName,
        String productSku,
        String productImage,
        int quantity,
        double revenue,
        int views,
        double conversionRate
) {}