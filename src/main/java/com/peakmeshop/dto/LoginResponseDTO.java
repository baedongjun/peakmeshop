package com.peakmeshop.dto;

/**
 * 로그인 응답 DTO
 */
public record LoginResponseDTO(
        String token,
        MemberDTO member
) {}