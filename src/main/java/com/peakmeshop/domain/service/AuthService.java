package com.peakmeshop.domain.service;

import java.util.Map;

import com.peakmeshop.api.dto.*;
import com.peakmeshop.domain.entity.Member;
import jakarta.validation.Valid;

public interface AuthService {

    AuthResponseDTO login(@Valid LoginRequest loginRequest);

    AuthResponseDTO signup(@Valid SignupRequest signupRequest);

    Map<String, Object> getUserInfo();

    void requestPasswordReset(String email);

    void resetPassword(@Valid PasswordResetDTO resetDTO);

    boolean verifyEmail(String token);

    void logout();

    AuthResponseDTO refreshToken(String refreshToken);

    void register(AuthDTO.Register register);

    void resendVerification(AuthDTO.ResendVerification resendVerification);
}