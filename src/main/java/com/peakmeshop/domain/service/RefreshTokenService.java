package com.peakmeshop.domain.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.domain.entity.Member;
import com.peakmeshop.domain.entity.RefreshToken;
import com.peakmeshop.common.exception.TokenRefreshException;
import com.peakmeshop.domain.repository.MemberRepository;
import com.peakmeshop.domain.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {

    @Value("${app.jwt.refresh-expiration}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, MemberRepository memberRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.memberRepository = memberRepository;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(Long memberId) {
        RefreshToken refreshToken = new RefreshToken();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다: " + memberId));

        // 기존 토큰이 있으면 삭제
        refreshTokenRepository.deleteByMemberId(memberId);

        refreshToken.setMember(member);
        refreshToken.setExpiryAt(LocalDateTime.ofInstant(Instant.now().plusMillis(refreshTokenDurationMs), ZoneId.systemDefault()));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        // Instant를 LocalDateTime으로 변환
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        if (token.getExpiryAt().isBefore(now)) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    public void deleteByMemberId(Long memberId) {
        refreshTokenRepository.deleteByMemberId(memberId);
    }
}