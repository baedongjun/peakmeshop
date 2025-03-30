package com.peakmeshop.dto;

import java.util.Set;

public record JwtAuthResponseDTO(
        String accessToken,
        String refreshToken,
        long expiresIn,
        String tokenType,
        Long id,
        String userId,
        String email,
        Set<String> roles
) {}