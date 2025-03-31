package com.peakmeshop.domain.service;

import java.util.Map;

import com.peakmeshop.api.dto.AuthResponseDTO;
import com.peakmeshop.api.dto.LoginRequest;
import com.peakmeshop.api.dto.PasswordResetDTO;
import com.peakmeshop.api.dto.SignupRequest;

public interface AuthService {

    AuthResponseDTO login(LoginRequest loginRequest);

    AuthResponseDTO signup(SignupRequest signupRequest);

    Map<String, Object> getUserInfo();

    void requestPasswordReset(String email);

    void resetPassword(PasswordResetDTO resetDTO);

    boolean verifyEmail(String token);

    void logout();

    AuthResponseDTO refreshToken(String refreshToken);
}