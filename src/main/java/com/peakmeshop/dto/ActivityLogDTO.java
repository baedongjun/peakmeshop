package com.peakmeshop.dto;

import java.time.LocalDateTime;

public record ActivityLogDTO(
        Long id,
        Long memberId,
        String actionType,
        String entityType,
        Long entityId,
        String description,
        String ipAddress,
        String userAgent,
        LocalDateTime createdAt
) {}