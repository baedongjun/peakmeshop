package com.peakmeshop.domain.service.impl;

import com.peakmeshop.api.dto.*;
import com.peakmeshop.common.security.JwtTokenProvider;
import com.peakmeshop.common.security.oauth2.user.UserPrincipal;
import com.peakmeshop.domain.entity.Member;
import com.peakmeshop.domain.entity.PasswordResetToken;
import com.peakmeshop.domain.entity.RefreshToken;
import com.peakmeshop.domain.entity.VerificationToken;
import com.peakmeshop.domain.repository.MemberRepository;
import com.peakmeshop.domain.repository.PasswordResetTokenRepository;
import com.peakmeshop.domain.repository.RefreshTokenRepository;
import com.peakmeshop.domain.repository.VerificationTokenRepository;
import com.peakmeshop.domain.service.AuthService;
import com.peakmeshop.domain.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Validated
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           MemberRepository memberRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider,
                           RefreshTokenRepository refreshTokenRepository,
                           VerificationTokenRepository verificationTokenRepository,
                           PasswordResetTokenRepository passwordResetTokenRepository,
                           EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public AuthResponseDTO login(@Valid LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUserId(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String accessToken = jwtTokenProvider.generateToken(userPrincipal);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userPrincipal);

        // 리프레시 토큰 저장
        saveRefreshToken(userPrincipal.getId(), refreshToken);

        // 마지막 로그인 시간 업데이트
        Member member = memberRepository.findById(userPrincipal.getId()).orElseThrow();
        member.setLastLoginAt(LocalDateTime.now());
        memberRepository.save(member);

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationMs / 1000)
                .userId(userPrincipal.getUserId())
                .email(userPrincipal.getEmail())
                .name(userPrincipal.getName())
                .role(userPrincipal.getAuthorities().iterator().next().getAuthority())
                .build();
    }

    @Override
    @Transactional
    public AuthResponseDTO signup(@Valid SignupRequest signupRequest) {
        // 아이디 중복 확인
        if (memberRepository.existsByUserId(signupRequest.getUserId())) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
        }

        // 이메일 중복 확인
        if (memberRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 일치 확인
        if (!signupRequest.getPassword().equals(signupRequest.getPasswordConfirm())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 회원 엔티티 생성
        Member member = new Member();
        member.setUserId(signupRequest.getUserId());
        member.setEmail(signupRequest.getEmail());
        member.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        member.setName(signupRequest.getName());
        member.setPhone(signupRequest.getPhone());
        member.setUserRole("ROLE_USER");
        member.setStatus("INACTIVE"); // 이메일 인증 전까지는 비활성 상태
        member.setCreatedAt(LocalDateTime.now());

        Member savedMember = memberRepository.save(member);

        // 이메일 인증 토큰 생성
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setMember(savedMember);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24)); // 24시간 유효
        verificationTokenRepository.save(verificationToken);

        // 인증 이메일 발송
        emailService.sendVerificationEmail(savedMember.getEmail(), savedMember.getName(), token);

        // 자동 로그인 처리
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signupRequest.getUserId(),
                        signupRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String accessToken = jwtTokenProvider.generateToken(userPrincipal);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userPrincipal);

        // 리프레시 토큰 저장
        saveRefreshToken(userPrincipal.getId(), refreshToken);

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationMs / 1000)
                .userId(userPrincipal.getUserId())
                .email(userPrincipal.getEmail())
                .name(userPrincipal.getName())
                .role(userPrincipal.getAuthorities().iterator().next().getAuthority())
                .build();
    }

    @Override
    public Map<String, Object> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Member member = memberRepository.findById(userPrincipal.getId()).orElseThrow();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", member.getId());
        userInfo.put("userId", member.getUserId());
        userInfo.put("email", member.getEmail());
        userInfo.put("name", member.getName());
        userInfo.put("phone", member.getPhone());
        userInfo.put("role", member.getUserRole());
        userInfo.put("status", member.getStatus());
        userInfo.put("createdAt", member.getCreatedAt());
        userInfo.put("lastLoginAt", member.getLastLoginAt());

        return userInfo;
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당 이메일로 등록된 사용자가 없습니다."));

        // 기존 토큰 삭제
        passwordResetTokenRepository.deleteByMemberId(member.getId());

        // 새 토큰 생성
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setMember(member);
        passwordResetToken.setExpiryDate(LocalDateTime.now().plusHours(1)); // 1시간 유효
        passwordResetTokenRepository.save(passwordResetToken);

        // 비밀번호 재설정 이메일 발송
        String resetUrl = "http://localhost/auth/reset-password?token=" + token;
        emailService.sendPasswordResetRequestEmail(member.getEmail(), member.getName(), resetUrl);
    }

    @Override
    @Transactional
    public void resetPassword(@Valid PasswordResetDTO resetDTO) {
        if (!resetDTO.getNewPassword().equals(resetDTO.getConfirmPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(resetDTO.getToken())
                .orElseThrow(() -> new RuntimeException("유효하지 않은 토큰입니다."));

        if (passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("만료된 토큰입니다.");
        }

        Member member = passwordResetToken.getMember();
        member.setPassword(passwordEncoder.encode(resetDTO.getNewPassword()));
        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);

        // 토큰 삭제
        passwordResetTokenRepository.delete(passwordResetToken);
    }

    @Override
    @Transactional
    public boolean verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 토큰입니다."));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("만료된 토큰입니다.");
        }

        Member member = verificationToken.getMember();
        member.setStatus("ACTIVE");
        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);

        // 토큰 삭제
        verificationTokenRepository.delete(verificationToken);

        return true;
    }

    @Override
    @Transactional
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getPrincipal().equals("anonymousUser")) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            refreshTokenRepository.deleteByMemberId(userPrincipal.getId());
        }

        SecurityContextHolder.clearContext();
    }

    @Override
    @Transactional
    public AuthResponseDTO refreshToken(String refreshToken) {
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 리프레시 토큰입니다."));

        if (refreshTokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshTokenEntity);
            throw new RuntimeException("만료된 리프레시 토큰입니다.");
        }

        Member member = refreshTokenEntity.getMember();
        UserPrincipal userPrincipal = UserPrincipal.create(member);

        String newAccessToken = jwtTokenProvider.generateToken(userPrincipal);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userPrincipal);

        // 리프레시 토큰 업데이트
        refreshTokenEntity.setToken(newRefreshToken);
        refreshTokenEntity.setExpiryDate(LocalDateTime.now().plusDays(7)); // 7일 유효
        refreshTokenRepository.save(refreshTokenEntity);

        return AuthResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationMs / 1000)
                .userId(userPrincipal.getUserId())
                .email(userPrincipal.getEmail())
                .name(userPrincipal.getName())
                .role(userPrincipal.getAuthorities().iterator().next().getAuthority())
                .build();
    }

    @Override
    public void register(AuthDTO.Register register) {

    }

    @Override
    public void resendVerification(AuthDTO.ResendVerification resendVerification) {

    }

    private void saveRefreshToken(Long memberId, String token) {
        // 기존 토큰 삭제
        refreshTokenRepository.deleteByMemberId(memberId);

        // 새 토큰 저장
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setMember(memberRepository.findById(memberId).orElseThrow());
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7)); // 7일 유효
        refreshToken.setCreatedAt(LocalDateTime.now());
        refreshTokenRepository.save(refreshToken);
    }
}

