package com.peakmeshop.dto;

import java.time.LocalDateTime;

public record BrandDTO(
        Long id,
        String name,
        String slug,
        String description,
        String logo,
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