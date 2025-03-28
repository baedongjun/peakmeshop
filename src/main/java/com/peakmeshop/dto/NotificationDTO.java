package com.peakmeshop.dto;

import java.time.LocalDateTime;

/**
 * 알림 정보를 전송하기 위한 DTO
 */
public record NotificationDTO(
        Long id,
        Long memberId,
        String title,
        String message,
        String type, // ORDER, PROMOTION, SYSTEM
        String link,
        Boolean isRead,
        LocalDateTime createdAt,
        LocalDateTime readAt
) {}