package com.peakmeshop.domain.service;

import java.util.Map;

import com.peakmeshop.api.dto.*;
import com.peakmeshop.domain.entity.Member;
import jakarta.validation.Valid;

public interface AuthService {

    AuthResponseDTO login(LoginRequest loginRequest);

    public Member register(MemberDTO memberDTO);

    AuthResponseDTO signup(SignupRequest signupRequest);

    Map<String, Object> getUserInfo();

    void requestPasswordReset(String email);

    void resetPassword(PasswordResetDTO resetDTO);

    boolean verifyEmail(String token);

    void logout();

    AuthResponseDTO refreshToken(String refreshToken);
}