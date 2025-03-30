package com.peakmeshop.service;

import java.util.Map;

import com.peakmeshop.dto.AuthResponseDTO;
import com.peakmeshop.dto.LoginRequest;
import com.peakmeshop.dto.PasswordResetDTO;
import com.peakmeshop.dto.SignupRequest;

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