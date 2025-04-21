package com.peakmeshop.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CouponDTO {

    private Long id;
    private String code;
    private String name;
    private String description;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscountAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer usageLimit;
    private Integer usedCount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double discountAmount;
    private Double discountPercentage;
    private Double minimumPurchaseAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Boolean isUsed;
    private String couponType;

    public boolean isExpiringSoon() {
        if (endDate == null) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(endDate)) {
            return false;
        }
        
        // 7일 이내에 만료되는 쿠폰을 "곧 만료" 상태로 간주
        long daysUntilExpiry = ChronoUnit.DAYS.between(now, endDate);
        return daysUntilExpiry <= 7;
    }
}