package com.peakmeshop.api.dto;

public record ReviewImageDTO(
        Long id,
        Long reviewId,
        String url,
        String alt,
        int displayOrder
) {}