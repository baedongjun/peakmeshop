package com.peakmeshop.api.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetTokenDTO {
    private Long id;
    private String token;
    private Long memberId;
    private String memberEmail;
    private LocalDateTime expiryAt;
    private LocalDateTime createdAt;
}