package com.peakmeshop.dto;

import java.time.LocalDateTime;

public record PromotionDTO(
        Long id,
        String name,
        String description,
        String discountType,
        double discountValue,
        LocalDateTime startDate,
        LocalDateTime endDate,
        boolean isActive,
        String bannerImageUrl,
        String promotionCode,
        Long categoryId,
        String categoryName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    // 생성자 오버로딩 - 새 프로모션 생성 시 사용
    public PromotionDTO(String name, String description, String discountType, double discountValue,
                        LocalDateTime startDate, LocalDateTime endDate, boolean isActive,
                        String bannerImageUrl, String promotionCode, Long categoryId) {
        this(null, name, description, discountType, discountValue, startDate, endDate,
                isActive, bannerImageUrl, promotionCode, categoryId, null, null, null);
    }
}