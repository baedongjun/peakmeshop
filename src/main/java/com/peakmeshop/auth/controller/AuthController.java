package com.peakmeshop.auth.controller;

import com.peakmeshop.api.dto.LoginRequest;
import com.peakmeshop.api.dto.SignupRequest;
import com.peakmeshop.domain.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("signupRequest", new SignupRequest());
        return "auth/register";
    }

    @GetMapping("/find-password")
    public String findPasswordForm() {
        return "auth/find-password";
    }

    /**
     * 아이디 찾기 페이지
     */
    @GetMapping("/find-id")
    public String findId() {
        return "shop/find-id";
    }

    /**
     * 비밀번호 재설정 페이지
     */
    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "shop/reset-password";
    }

    /**
     * 이메일 인증 페이지
     */
    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "shop/verify-email";
    }
}

