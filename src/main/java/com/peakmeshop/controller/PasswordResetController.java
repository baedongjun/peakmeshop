package com.peakmeshop.controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peakmeshop.dto.PasswordResetRequestDTO;
import com.peakmeshop.dto.PasswordResetTokenDTO;
import com.peakmeshop.service.EmailService;
import com.peakmeshop.service.MemberService;
import com.peakmeshop.service.PasswordResetTokenService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    private final MemberService memberService;
    private final PasswordResetTokenService tokenService;
    private final EmailService emailService;

    public PasswordResetController(
            MemberService memberService,
            PasswordResetTokenService tokenService,
            EmailService emailService) {
        this.memberService = memberService;
        this.tokenService = tokenService;
        this.emailService = emailService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        return memberService.findByEmail(email)
                .map(member -> {
                    try {
                        // 토큰 생성
                        String token = UUID.randomUUID().toString();
                        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);

                        PasswordResetTokenDTO tokenDTO = new PasswordResetTokenDTO(
                                null, member.id(), token, expiryDate);

                        tokenService.saveToken(tokenDTO);

                        // 이메일 전송
                        String resetLink = "https://peakmeshop.com/reset-password?token=" + token;
                        emailService.sendPasswordResetLink(email, member.name(), resetLink);

                        return ResponseEntity.ok(Map.of(
                                "message", "비밀번호 재설정 링크가 이메일로 전송되었습니다."));

                    } catch (MessagingException e) {
                        return ResponseEntity.internalServerError().body(Map.of(
                                "message", "이메일 전송 중 오류가 발생했습니다."));
                    }
                })
                .orElse(ResponseEntity.ok(Map.of(
                        "message", "비밀번호 재설정 링크가 이메일로 전송되었습니다.")));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetRequestDTO request) {
        boolean result = tokenService.validateToken(request.token());

        if (!result) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "유효하지 않거나 만료된 토큰입니다."));
        }

        Long memberId = tokenService.getMemberIdByToken(request.token());
        memberService.resetPassword(memberId, request.password());
        tokenService.deleteToken(request.token());

        return ResponseEntity.ok(Map.of(
                "message", "비밀번호가 성공적으로 재설정되었습니다."));
    }
}