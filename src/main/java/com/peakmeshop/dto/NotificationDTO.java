package com.peakmeshop.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private Long id;
    private Long memberId;
    private String title;
    private String message;
    private String type;
    private boolean read;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}