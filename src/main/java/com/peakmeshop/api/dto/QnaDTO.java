package com.peakmeshop.api.dto;

import java.time.LocalDateTime;

public record QnaDTO(
        Long id,
        Long memberId,
        String memberName,
        Long productId,
        String productName,
        String title,
        String content,
        boolean isSecret,
        String status,
        String answer,
        Long answeredById,
        String answeredByName,
        LocalDateTime answeredAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    // 생성자 오버로딩 - 새 Q&A 생성 시 사용
    public QnaDTO(Long memberId, Long productId, String title, String content, boolean isSecret) {
        this(null, memberId, null, productId, null, title, content, isSecret, null, null, null, null, null, null, null);
    }
}