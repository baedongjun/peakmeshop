package com.peakmeshop.auth.controller;

import com.peakmeshop.api.dto.LoginDTO;
import com.peakmeshop.api.dto.MemberDTO;
import com.peakmeshop.domain.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute MemberDTO memberDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // 비밀번호와 비밀번호 확인이 일치하는지 검증
        if (!memberDTO.getPassword().equals(memberDTO.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "error.memberDTO", "비밀번호가 일치하지 않습니다.");
        }

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            authService.register(memberDTO);
            redirectAttributes.addFlashAttribute("successMessage", "회원가입이 완료되었습니다. 로그인해주세요.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/auth/register";
        }
    }

    @GetMapping("/find-password")
    public String findPasswordForm() {
        return "auth/find-password";
    }
}

