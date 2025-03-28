package com.peakmeshop.dto;

import java.time.LocalDateTime;

/**
 * 비밀번호 재설정 토큰 정보를 전송하기 위한 DTO
 */
public record PasswordResetTokenDTO(
        Long id,
        Long memberId,
        String token,
        LocalDateTime expiryDate
) {}