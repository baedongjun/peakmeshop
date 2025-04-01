package com.peakmeshop.auth.controller;

import com.peakmeshop.api.dto.LoginDTO;
import com.peakmeshop.api.dto.MemberDTO;
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
        model.addAttribute("loginDTO", new LoginDTO());
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("memberDTO", new MemberDTO());
        return "auth/register";
    }

    @GetMapping("/find-password")
    public String findPasswordForm() {
        return "auth/find-password";
    }
}

