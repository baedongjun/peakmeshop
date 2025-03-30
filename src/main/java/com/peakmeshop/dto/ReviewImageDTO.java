package com.peakmeshop.dto;

public record ReviewImageDTO(
        Long id,
        Long reviewId,
        String url,
        String alt,
        int displayOrder
) {}