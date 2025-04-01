package com.peakmeshop.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.domain.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByMemberId(Long memberId);

    // 만료일이 특정 날짜 이전인 토큰 목록 조회
    List<PasswordResetToken> findByExpiryDateBefore(LocalDateTime date);

    void deleteByMemberId(Long memberId);
}