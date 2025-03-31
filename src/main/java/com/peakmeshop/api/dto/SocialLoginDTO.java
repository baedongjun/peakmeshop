package com.peakmeshop.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLoginDTO {

    private String provider;
    private String providerId;
    private String email;
    private String name;
    private String imageUrl;
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
}