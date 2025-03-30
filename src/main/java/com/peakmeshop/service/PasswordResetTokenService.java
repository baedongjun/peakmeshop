package com.peakmeshop.service;

import com.peakmeshop.dto.PasswordResetTokenDTO;
import com.peakmeshop.entity.PasswordResetToken;

public interface PasswordResetTokenService {

    PasswordResetTokenDTO createToken(Long memberId);

    PasswordResetTokenDTO getTokenByToken(String token);

    void validateToken(String token);

    void deleteToken(String token);

    void deleteTokensByMemberId(Long memberId);

    void cleanExpiredTokens();
}