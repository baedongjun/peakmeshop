package com.peakmeshop.api.dto;

/**
 * 토큰 갱신 요청 DTO
 */
public record TokenRefreshRequestDTO(
        String refreshToken
) {}