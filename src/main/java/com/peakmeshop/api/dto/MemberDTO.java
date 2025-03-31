package com.peakmeshop.api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {

    private Long id;
    private String userId;
    private String email;
    private String password;
    private String name;
    private String phone;
    private String userRole;
    private boolean enabled;
    private String status;
    private boolean emailVerified;
    private String provider;
    private String providerId;
    private String providerType;
    private String imageUrl;
    private LocalDate birthDate;
    private String gender;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}