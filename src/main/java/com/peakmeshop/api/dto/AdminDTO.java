package com.peakmeshop.api.dto;

import java.time.LocalDateTime;

public record AdminDTO(
    Long id,
    String username,
    String email,
    String name,
    String role,
    String status,
    LocalDateTime lastLogin,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 