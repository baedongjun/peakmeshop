package com.peakmeshop.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "토큰은 필수 입력값입니다.")
    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @NotNull(message = "만료일은 필수 입력값입니다.")
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}