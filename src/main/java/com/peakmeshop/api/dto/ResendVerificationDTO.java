package com.peakmeshop.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendVerificationDTO(
        @NotBlank @Email String email
) {}