package com.peakmeshop.api.dto;

import java.time.LocalDateTime;

import com.peakmeshop.api.dto.CouponDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberCouponDTO {

    private Long id;
    private Long memberId;
    private CouponDTO coupon;
    private boolean used;
    private LocalDateTime issuedAt;
    private LocalDateTime usedAt;
    private LocalDateTime endDate;
}