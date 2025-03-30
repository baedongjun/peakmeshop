package com.peakmeshop.dto;

import java.time.LocalDateTime;
import java.util.List;

public record WishlistDTO(
        Long id,
        Long memberId,
        String name,
        boolean isDefault,
        boolean isPublic,
        String shareUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<WishlistItemDTO> items
) {}