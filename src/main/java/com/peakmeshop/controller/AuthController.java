package com.peakmeshop.controller;

import com.peakmeshop.dto.*;
import com.peakmeshop.entity.RefreshToken;
import com.peakmeshop.exception.TokenRefreshException;
import com.peakmeshop.service.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peakmeshop.security.JwtTokenProvider;
import com.peakmeshop.service.MemberService;

import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            MemberService memberService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.memberService = memberService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            // 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    )
            );

            // 인증 성공 시 SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // JWT 토큰 생성
            String token = jwtTokenProvider.generateToken(authentication);

            // 로그인 시간 업데이트
            memberService.updateLastLoginTime(loginRequest.email());

            // 회원 정보 조회
            MemberDTO member = memberService.findByEmail(loginRequest.email())
                    .orElseThrow(() -> new IllegalStateException("인증된 사용자를 찾을 수 없습니다."));

            // 응답 생성
            LoginResponseDTO response = new LoginResponseDTO(token, member);

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "이메일 또는 비밀번호가 일치하지 않습니다."));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequestDTO request) {
        String requestRefreshToken = request.refreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getMember)
                .map(member -> {
                    String token = jwtTokenProvider.generateTokenFromUsername(member.getEmail());

                    return ResponseEntity.ok(new TokenRefreshResponseDTO(
                            token,
                            requestRefreshToken,
                            "Bearer"));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            String email = userDetails.getUsername();
            memberService.findByEmail(email).ifPresent(member ->
                    refreshTokenService.deleteByMemberId(member.id()));
            return ResponseEntity.ok(Map.of("message", "로그아웃 되었습니다."));
        }

        return ResponseEntity.badRequest().body(Map.of("message", "로그인 상태가 아닙니다."));
    }
}