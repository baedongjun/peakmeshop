package com.peakmeshop.domain.service.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.AuthResponseDTO;
import com.peakmeshop.api.dto.LoginRequest;
import com.peakmeshop.api.dto.PasswordResetDTO;
import com.peakmeshop.api.dto.SignupRequest;
import com.peakmeshop.domain.entity.Member;
import com.peakmeshop.common.exception.BadRequestException;
import com.peakmeshop.common.exception.ResourceNotFoundException;
import com.peakmeshop.domain.repository.MemberRepository;
import com.peakmeshop.common.security.JwtTokenProvider;
import com.peakmeshop.common.security.oauth2.user.UserPrincipal;
import com.peakmeshop.domain.service.AuthService;
import com.peakmeshop.domain.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final EmailService emailService;

    @Override
    public AuthResponseDTO login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String token = tokenProvider.generateToken(userPrincipal);

        // 마지막 로그인 시간 업데이트
        Member member = memberRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));
        member.setLastLoginAt(LocalDateTime.now());
        memberRepository.save(member);

        return AuthResponseDTO.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .id(userPrincipal.getId())
                .email(userPrincipal.getEmail())
                .name(userPrincipal.getName())
                .roles(userPrincipal.getRoles())
                .build();
    }

    @Override
    @Transactional
    public AuthResponseDTO signup(SignupRequest signupRequest) {
        // 이메일 중복 확인
        if (memberRepository.existsByEmail(signupRequest.getEmail())) {
            throw new BadRequestException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        // 인증 토큰 생성
        String verificationToken = UUID.randomUUID().toString();

        // 회원 생성
        Member member = Member.builder()
                .email(signupRequest.getEmail())
                .password(encodedPassword)
                .name(signupRequest.getName())
                .phone(signupRequest.getPhone())
                .userRole("ROLE_USER")
                .enabled(false)
                .status(Member.STATUS_INACTIVE)
                .emailVerified(false)
                .verificationToken(verificationToken)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Member savedMember = memberRepository.save(member);

        // 인증 이메일 발송
        String verificationUrl = "https://peakmeshop.com/verify-email?token=" + verificationToken;
        emailService.sendVerificationEmail(savedMember.getEmail(), verificationToken, verificationUrl);

        // 토큰 생성
        String token = tokenProvider.generateToken(savedMember.getId());

        return AuthResponseDTO.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .id(savedMember.getId())
                .email(savedMember.getEmail())
                .name(savedMember.getName())
                .roles(Collections.singletonList("ROLE_USER"))
                .build();
    }

    @Override
    public Map<String, Object> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", userPrincipal.getId());
        userInfo.put("email", userPrincipal.getEmail());
        userInfo.put("name", userPrincipal.getName());
        userInfo.put("roles", userPrincipal.getRoles());

        return userInfo;
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

        // 이미 발급된 토큰이 있고 아직 유효한 경우
        if (member.getResetToken() != null && member.getResetTokenExpiry() != null &&
                member.getResetTokenExpiry().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("이미 비밀번호 재설정 요청이 처리되었습니다. 이메일을 확인해주세요.");
        }

        // 토큰 생성
        String resetToken = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(24);

        // 토큰 저장
        member.setResetToken(resetToken);
        member.setResetTokenExpiry(expiry);
        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);

        // 이메일 발송
        String resetUrl = "https://peakmeshop.com/reset-password?token=" + resetToken;
        emailService.sendPasswordResetRequestEmail(email, resetToken, resetUrl);
    }

    @Override
    @Transactional
    public void resetPassword(PasswordResetDTO resetDTO) {
        Member member = memberRepository.findByResetToken(resetDTO.getToken())
                .orElseThrow(() -> new ResourceNotFoundException("유효하지 않은 토큰입니다."));

        // 토큰 만료 확인
        if (member.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            // 만료된 토큰 제거
            member.setResetToken(null);
            member.setResetTokenExpiry(null);
            memberRepository.save(member);

            throw new BadRequestException("만료된 토큰입니다. 비밀번호 재설정을 다시 요청해주세요.");
        }

        // 비밀번호 변경
        member.setPassword(passwordEncoder.encode(resetDTO.getNewPassword()));
        member.setResetToken(null);
        member.setResetTokenExpiry(null);
        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public boolean verifyEmail(String token) {
        Member member = memberRepository.findByVerificationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("유효하지 않은 토큰입니다."));

        // 이미 인증된 계정인지 확인
        if (member.isEmailVerified()) {
            return true;
        }

        // 계정 활성화
        member.setEmailVerified(true);
        member.setEnabled(true);
        member.setStatus(Member.STATUS_ACTIVE);
        member.setVerificationToken(null);
        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);

        return true;
    }

    @Override
    @Transactional
    public void logout() {
        // JWT는 서버에서 관리하지 않으므로 클라이언트에서 토큰을 삭제하는 방식으로 처리
        // 필요한 경우 토큰 블랙리스트 등을 구현할 수 있음
        SecurityContextHolder.clearContext();
    }

    @Override
    public AuthResponseDTO refreshToken(String refreshToken) {
        // 리프레시 토큰 검증 및 새 액세스 토큰 발급
        Long userId = tokenProvider.getUserIdFromToken(refreshToken);
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

        String newAccessToken = tokenProvider.generateToken(userId);

        return AuthResponseDTO.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .id(userId)
                .email(member.getEmail())
                .name(member.getName())
                .roles(Collections.singletonList(member.getUserRole()))
                .build();
    }
}