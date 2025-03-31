package com.peakmeshop.api.dto;

import java.time.LocalDateTime;

public record StockNotificationDTO(
        Long id,
        Long productId,
        String productName,
        String productImage,
        Long memberId,
        String memberName,
        String memberEmail,
        LocalDateTime createdAt,
        LocalDateTime notifiedAt,
        boolean isActive
) {}