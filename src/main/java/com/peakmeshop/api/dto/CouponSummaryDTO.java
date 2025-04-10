package com.peakmeshop.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponSummaryDTO {
    private long totalCoupons;
    private long availableCoupons;
    private long monthlyUsedCoupons;
    private long totalDiscountAmount;
} 