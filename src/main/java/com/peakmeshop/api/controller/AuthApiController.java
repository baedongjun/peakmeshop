package com.peakmeshop.api.controller;

import com.peakmeshop.api.dto.AuthDTO;
import com.peakmeshop.api.dto.AuthResponseDTO;
import com.peakmeshop.api.dto.LoginRequest;
import com.peakmeshop.api.dto.SignupRequest;
import com.peakmeshop.domain.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@RequestBody String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        authService.logout();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> register(@RequestBody SignupRequest signupRequest) {
        authService.signup(signupRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password-reset")
    public ResponseEntity<Void> requestPasswordReset(@RequestParam String email) {
        authService.requestPasswordReset(email);
        return ResponseEntity.ok().build();
    }
} 