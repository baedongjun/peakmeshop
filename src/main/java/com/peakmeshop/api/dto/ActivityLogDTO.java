package com.peakmeshop.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogDTO {
    private Long id;
    private String type;
    private String description;
    private String referenceType;
    private Long referenceId;
    private String userAgent;
    private String ipAddress;
    private String additionalData;
    private String userId;
    private Long memberId;
    private LocalDateTime createdAt;
}