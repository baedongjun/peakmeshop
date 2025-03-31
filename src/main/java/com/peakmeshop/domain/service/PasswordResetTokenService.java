package com.peakmeshop.domain.service;

import com.peakmeshop.api.dto.PasswordResetTokenDTO;

public interface PasswordResetTokenService {

    PasswordResetTokenDTO createToken(Long memberId);

    PasswordResetTokenDTO getTokenByToken(String token);

    void validateToken(String token);

    void deleteToken(String token);

    void deleteTokensByMemberId(Long memberId);

    void cleanExpiredTokens();
}