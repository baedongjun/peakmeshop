package com.peakmeshop.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 로그인, 회원가입 등 인증 관련 뷰 컨트롤러
 */
@Controller
public class AuthViewController {

    /**
     * 로그인 페이지
     */
    @GetMapping("/login")
    public String login() {
        return "shop/login";
    }

    /**
     * 회원가입 페이지
     */
    @GetMapping("/register")
    public String register() {
        return "shop/register";
    }

    /**
     * 아이디 찾기 페이지
     */
    @GetMapping("/find-id")
    public String findId() {
        return "shop/find-id";
    }

    /**
     * 비밀번호 찾기 페이지
     */
    @GetMapping("/find-password")
    public String findPassword() {
        return "shop/find-password";
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

