package com.peakmeshop.dto;

import java.time.LocalDateTime;

public record ReviewCommentDTO(
        Long id,
        Long reviewId,
        Long memberId,
        String memberName,
        String content,
        boolean isAdmin,
        boolean isHidden,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}