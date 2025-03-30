package com.peakmeshop.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.dto.PasswordResetTokenDTO;
import com.peakmeshop.entity.Member;
import com.peakmeshop.entity.PasswordResetToken;
import com.peakmeshop.exception.BadRequestException;
import com.peakmeshop.exception.ResourceNotFoundException;
import com.peakmeshop.repository.MemberRepository;
import com.peakmeshop.repository.PasswordResetTokenRepository;
import com.peakmeshop.service.PasswordResetTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    private final PasswordResetTokenRepository tokenRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public PasswordResetTokenDTO createToken(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));

        // 기존 토큰 삭제
        tokenRepository.deleteByMemberId(memberId);

        // 새 토큰 생성
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .member(member)
                .token(token)
                .expiryDate(LocalDateTime.now().plusHours(24)) // 24시간 유효
                .build();

        PasswordResetToken savedToken = tokenRepository.save(resetToken);

        return PasswordResetTokenDTO.builder()
                .id(savedToken.getId())
                .token(savedToken.getToken())
                .memberId(savedToken.getMember().getId())
                .expiryDate(savedToken.getExpiryDate())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PasswordResetTokenDTO getTokenByToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found: " + token));

        return PasswordResetTokenDTO.builder()
                .id(resetToken.getId())
                .token(resetToken.getToken())
                .memberId(resetToken.getMember().getId())
                .expiryDate(resetToken.getExpiryDate())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public void validateToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid password reset token"));

        // 토큰 만료 확인
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Password reset token has expired");
        }
    }

    @Override
    @Transactional
    public void deleteToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found: " + token));

        tokenRepository.delete(resetToken);
    }

    @Override
    @Transactional
    public void deleteTokensByMemberId(Long memberId) {
        tokenRepository.deleteByMemberId(memberId);
    }

    @Override
    @Scheduled(cron = "0 0 1 * * ?") // 매일 새벽 1시에 실행
    @Transactional
    public void cleanExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        List<PasswordResetToken> expiredTokens = tokenRepository.findByExpiryDateBefore(now);

        if (!expiredTokens.isEmpty()) {
            tokenRepository.deleteAll(expiredTokens);
            log.info("Cleaned {} expired password reset tokens", expiredTokens.size());
        }
    }
}