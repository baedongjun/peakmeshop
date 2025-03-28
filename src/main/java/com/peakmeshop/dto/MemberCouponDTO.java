package com.peakmeshop.dto;

import java.time.LocalDateTime;

/**
 * 회원 쿠폰 정보를 전송하기 위한 DTO
 */
public record MemberCouponDTO(
        Long id,
        Long memberId,
        CouponDTO coupon,
        Boolean isUsed,
        LocalDateTime usedAt,
        LocalDateTime issuedAt,
        Long orderId
) {}