package com.peakmeshop.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "verification_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "토큰은 필수 입력값입니다.")
    @Column(nullable = false, unique = true)
    private String token;

    @NotNull(message = "회원 정보는 필수 입력값입니다.")
    @OneToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @NotNull(message = "만료일은 필수 입력값입니다.")
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;
}