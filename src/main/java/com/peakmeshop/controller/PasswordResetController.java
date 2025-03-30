package com.peakmeshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peakmeshop.dto.ApiResponse;
import com.peakmeshop.dto.PasswordResetDTO;
import com.peakmeshop.dto.PasswordResetRequestDTO;
import com.peakmeshop.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
@Slf4j
public class PasswordResetController {

    private final MemberService memberService;

    @PostMapping("/request-reset")
    public ResponseEntity<ApiResponse> requestPasswordReset(@RequestBody PasswordResetRequestDTO requestDTO) {
        memberService.requestPasswordReset(requestDTO);
        return ResponseEntity.ok(new ApiResponse(true, "비밀번호 재설정 이메일이 발송되었습니다."));
    }

    @PostMapping("/reset")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody PasswordResetDTO resetDTO) {
        memberService.resetPassword(resetDTO);
        return ResponseEntity.ok(new ApiResponse(true, "비밀번호가 재설정되었습니다."));
    }
}