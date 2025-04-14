package com.peakmeshop.api.dto;

import com.peakmeshop.domain.entity.Banner;
import java.time.LocalDateTime;

public record BannerDTO(
    Long id,
    String title,
    String subtitle,
    String description,
    String imageUrl,
    String linkUrl,
    String position,
    Banner.BannerStatus status,
    Integer sortOrder,
    LocalDateTime startDate,
    LocalDateTime endDate,
    boolean isActive,
    String backgroundColor,
    String textColor,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}