package com.peakmeshop.api.dto;

import java.time.LocalDateTime;
import java.util.List;

public record WishlistDTO(
        Long id,
        Long memberId,
        boolean isDefault,
        boolean isPublic,
        String shareUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<WishlistItemDTO> items
) {}