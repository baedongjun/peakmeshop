package com.peakmeshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 비밀번호 재설정 요청을 위한 DTO
 */
public record PasswordResetRequestDTO(
        @NotBlank(message = "토큰은 필수입니다")
        String token,

        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
        String password
) {}