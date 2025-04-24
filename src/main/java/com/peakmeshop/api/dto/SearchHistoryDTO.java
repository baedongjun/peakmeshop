package com.peakmeshop.api.dto;

import java.time.LocalDateTime;

public record SearchHistoryDTO(
    Long id,
    Long memberId,
    String keyword,
    String category,
    LocalDateTime createdAt
) {} 