package com.peakmeshop.api.dto;

import com.peakmeshop.api.dto.MemberDTO;

/**
 * 로그인 응답 DTO
 */
public record LoginResponseDTO(
        String token,
        MemberDTO member
) {}