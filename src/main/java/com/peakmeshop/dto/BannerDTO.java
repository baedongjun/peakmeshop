package com.peakmeshop.dto;

import java.time.LocalDateTime;

public record BannerDTO(
        Long id,
        String title,
        String subtitle,
        String imageUrl,
        String linkUrl,
        String position,
        LocalDateTime startDate,
        LocalDateTime endDate,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    // 생성자 오버로딩 - 새 배너 생성 시 사용
    public BannerDTO(String title, String subtitle, String imageUrl, String linkUrl, String position,
                     LocalDateTime startDate, LocalDateTime endDate, boolean isActive) {
        this(null, title, subtitle, imageUrl, linkUrl, position, startDate, endDate, isActive, null, null);
    }
}