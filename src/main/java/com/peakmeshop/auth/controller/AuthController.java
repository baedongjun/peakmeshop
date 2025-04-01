package com.peakmeshop.auth.controller;

import com.peakmeshop.api.dto.LoginRequest;
import com.peakmeshop.api.dto.SignupRequest;
import com.peakmeshop.domain.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}

