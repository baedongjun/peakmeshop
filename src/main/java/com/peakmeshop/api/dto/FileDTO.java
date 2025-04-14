package com.peakmeshop.api.dto;

import java.time.LocalDateTime;

public record FileDTO(
    Long id,
    String originalName,
    String fileName,
    String filePath,
    String fileType,
    Long fileSize,
    String mimeType,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}