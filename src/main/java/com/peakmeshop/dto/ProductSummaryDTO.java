package com.peakmeshop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 상품 요약 정보를 전송하기 위한 DTO
 */
public record ProductSummaryDTO(
        Long id,
        String name,
        String slug,
        String thumbnailUrl,
        BigDecimal price,
        BigDecimal discountPrice,
        Boolean isDiscounted,
        Double discountRate,
        Integer stockQuantity,
        Boolean isAvailable,
        String categoryName,
        Long categoryId,
        Double averageRating,
        Integer reviewCount,
        Integer orderCount,
        LocalDateTime createdAt
) {
    /**
     * 할인율 계산
     * @param originalPrice 원래 가격
     * @param discountPrice 할인 가격
     * @return 할인율
     */
    public static Double calculateDiscountRate(BigDecimal originalPrice, BigDecimal discountPrice) {
        if (originalPrice == null || discountPrice == null || originalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return 0.0;
        }

        if (discountPrice.compareTo(originalPrice) >= 0) {
            return 0.0;
        }

        BigDecimal discount = originalPrice.subtract(discountPrice);
        return discount.divide(originalPrice, 2, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
    }

    /**
     * 할인 여부 확인
     * @param originalPrice 원래 가격
     * @param discountPrice 할인 가격
     * @return 할인 여부
     */
    public static Boolean isDiscounted(BigDecimal originalPrice, BigDecimal discountPrice) {
        if (originalPrice == null || discountPrice == null) {
            return false;
        }

        return discountPrice.compareTo(originalPrice) < 0;
    }
}