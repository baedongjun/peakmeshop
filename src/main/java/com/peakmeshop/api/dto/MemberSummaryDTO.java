package com.peakmeshop.api.dto;

import java.time.LocalDateTime;

public record MemberSummaryDTO(
    // 전체 회원 통계
    long total,                  // 전체 회원 수
    long active,                 // 활성 회원 수
    long inactive,              // 비활성 회원 수
    long blocked,               // 차단된 회원 수
    long dormant,               // 휴면 회원 수

    // 개별 회원 통계
    Long totalPoints,           // 총 포인트
    Long totalOrders,          // 총 주문 수
    Long totalOrderAmount,     // 총 주문 금액
    LocalDateTime lastOrderDate // 마지막 주문일
) {
    public static MemberSummaryDTO createTotalSummary(
            long total,
            long active,
            long inactive,
            long blocked,
            long dormant
    ) {
        return new MemberSummaryDTO(
                total,
                active,
                inactive,
                blocked,
                dormant,
                null,
                null,
                null,
                null
        );
    }

    public static MemberSummaryDTO createMemberSummary(
            Long totalPoints,
            Long totalOrders,
            Long totalOrderAmount,
            LocalDateTime lastOrderDate
    ) {
        return new MemberSummaryDTO(
                0,
                0,
                0,
                0,
                0,
                totalPoints,
                totalOrders,
                totalOrderAmount,
                lastOrderDate
        );
    }
} 