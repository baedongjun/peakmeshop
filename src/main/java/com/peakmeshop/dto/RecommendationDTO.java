package com.peakmeshop.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RecommendationDTO(
        Long id,
        String type,
        Long memberId,
        Long productId,
        String productName,
        String productImage,
        double score,
        String reason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}