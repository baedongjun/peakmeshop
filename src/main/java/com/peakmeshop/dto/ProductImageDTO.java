package com.peakmeshop.dto;

public record ProductImageDTO(
        Long id,
        String url,
        String alt,
        int displayOrder,
        boolean isDefault,
        String thumbnailUrl,
        String mediumUrl,
        String largeUrl,
        String originalUrl,
        String mimeType,
        long fileSize,
        int width,
        int height
) {}