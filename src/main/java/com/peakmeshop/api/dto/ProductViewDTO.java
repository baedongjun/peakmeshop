package com.peakmeshop.api.dto;

import com.peakmeshop.api.dto.ProductDTO;

import java.time.LocalDateTime;

public record ProductViewDTO(
        Long id,
        ProductDTO product,
        Long memberId,
        String sessionId,
        String ipAddress,
        String userAgent,
        String referrer,
        LocalDateTime viewDate,
        LocalDateTime viewEndDate,
        int viewDurationSeconds,
        boolean isAddedToCart,
        boolean isPurchased
) {}