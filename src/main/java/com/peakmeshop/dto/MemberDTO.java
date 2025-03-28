package com.peakmeshop.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 회원 정보를 전송하기 위한 DTO
 */
public record MemberDTO(
        Long id,

        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "유효한 이메일 형식이 아닙니다")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
        String password,

        @NotBlank(message = "이름은 필수입니다")
        String name,

        @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "유효한 전화번호 형식이 아닙니다 (예: 010-1234-5678)")
        String phone,

        String address,

        String detailAddress,

        String zipCode,

        String role,

        Boolean isActive,

        LocalDateTime createdAt,

        LocalDateTime updatedAt,

        LocalDateTime lastLoginAt
) {
    // 빌더 패턴을 위한 정적 메서드
    public static Builder builder() {
        return new Builder();
    }

    // 비밀번호를 제외한 DTO 생성 (보안상 비밀번호 제외)
    public MemberDTO withoutPassword() {
        return new MemberDTO(
                this.id, this.email, null, this.name, this.phone,
                this.address, this.detailAddress, this.zipCode,
                this.role, this.isActive, this.createdAt, this.updatedAt, this.lastLoginAt
        );
    }

    // 빌더 클래스
    public static class Builder {
        private Long id;
        private String email;
        private String password;
        private String name;
        private String phone;
        private String address;
        private String detailAddress;
        private String zipCode;
        private String role;
        private Boolean isActive;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime lastLoginAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder detailAddress(String detailAddress) { this.detailAddress = detailAddress; return this; }
        public Builder zipCode(String zipCode) { this.zipCode = zipCode; return this; }
        public Builder role(String role) { this.role = role; return this; }
        public Builder isActive(Boolean isActive) { this.isActive = isActive; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder lastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; return this; }

        public MemberDTO build() {
            return new MemberDTO(id, email, password, name, phone, address, detailAddress,
                    zipCode, role, isActive, createdAt, updatedAt, lastLoginAt);
        }
    }
}