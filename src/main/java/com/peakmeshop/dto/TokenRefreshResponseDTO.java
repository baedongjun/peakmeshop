package com.peakmeshop.dto;

/**
 * 토큰 갱신 응답 DTO
 */
public record TokenRefreshResponseDTO(
        String accessToken,
        String refreshToken,
        String tokenType
) {}