package com.peakmeshop.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 상품 요약 정보를 전송하기 위한 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummaryDTO {
    private long totalProducts;
    private long activeProducts;
    private long outOfStockProducts;
    private long lowStockProducts;
    private long totalCategories;
    private long totalBrands;
    private double averagePrice;
    private long totalInventory;
    private long monthlyNewProducts;
    private long monthlyTopSellers;
    private double monthlyInventoryTurnover;

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