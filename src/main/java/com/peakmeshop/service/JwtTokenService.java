package com.peakmeshop.service;

import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;

public interface JwtTokenService {

    String generateToken(UserDetails userDetails);

    String generateToken(UserDetails userDetails, Map<String, Object> claims);

    boolean validateToken(String token, UserDetails userDetails);

    String extractUsername(String token);

    Claims extractAllClaims(String token);

    boolean isTokenExpired(String token);

    void invalidateToken(String token);
}