package com.peakmeshop.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "토큰은 필수 입력값입니다.")
    @Column(nullable = false, unique = true)
    private String token;

    @NotNull(message = "회원 정보는 필수 입력값입니다.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @NotNull(message = "만료일은 필수 입력값입니다.")
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "revoked_reason")
    private String revokedReason;

    // 토큰 만료 확인 메서드
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }

    // 토큰 취소 확인 메서드
    public boolean isRevoked() {
        return this.revokedAt != null;
    }

    // 토큰 취소 처리 메서드
    public void revoke(String reason) {
        this.revokedAt = LocalDateTime.now();
        this.revokedReason = reason;
    }
}