package com.peakmeshop.api.dto;

import com.peakmeshop.domain.entity.Email;
import java.time.LocalDateTime;
import java.util.Map;

public record EmailDTO(
    Long id,
    Long templateId,
    String recipient,
    String subject,
    String content,
    Email.EmailStatus status,
    String errorMessage,
    LocalDateTime createdAt,
    LocalDateTime sentAt
) {}