package com.peakmeshop.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthDTO() {
    public record Login(
            @NotBlank
            @Email
            String email,
            @NotBlank
            @Size(min = 6)
            String password
    ) {}

    public record Register(
            @NotBlank
            @Email
            String email,
            @NotBlank
            @Size(min = 6)
            String password,
            @NotBlank
            String name
    ) {}

    public record Token(
            String accessToken,
            String refreshToken
    ) {}

    public record Logout(
            String refreshToken
    ) {}

    public record RefreshToken(
            String refreshToken
    ) {}

    public record VerifyEmail(
            String token
    ) {}

    public record ResendVerification(
            @NotBlank
            @Email
            String email
    ) {}
} 