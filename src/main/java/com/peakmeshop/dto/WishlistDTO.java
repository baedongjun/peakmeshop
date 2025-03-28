package com.peakmeshop.dto;

import java.time.LocalDateTime;

/**
 * 위시리스트 정보를 전송하기 위한 DTO
 */
public record WishlistDTO(
        Long id,
        Long memberId,
        Long productId,
        String productName,
        String productImage,
        java.math.BigDecimal productPrice,
        Boolean isAvailable,
        LocalDateTime addedAt
) {}