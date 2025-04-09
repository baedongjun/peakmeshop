package com.peakmeshop.api.dto;

import java.time.LocalDateTime;

public record BrandDTO(
        Long id,
        String name,
        String nameEn,
        String slug,
        String description,
        String logoUrl,
        String website,
        boolean isActive,
        boolean isFeatured,
        int displayOrder,
        String metaTitle,
        String metaDescription,
        String metaKeywords,
        int productCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}