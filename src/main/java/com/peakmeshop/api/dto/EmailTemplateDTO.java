package com.peakmeshop.api.dto;

import com.peakmeshop.domain.entity.EmailTemplate;
import java.time.LocalDateTime;

/**
 * 이메일 템플릿 정보를 전송하기 위한 DTO
 */
public record EmailTemplateDTO(
    Long id,
    String code,
    String name,
    String subject,
    String content,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}