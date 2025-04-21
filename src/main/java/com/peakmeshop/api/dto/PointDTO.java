package com.peakmeshop.api.dto;

import java.time.LocalDateTime;

import com.peakmeshop.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointDTO {

    private Long id;
    private Member member;
    private String memberName;
    private String memberEmail;
    private Integer currentPoint;
    private Integer totalEarnedPoint;
    private Integer totalUsedPoint;
    private LocalDateTime updatedAt;
    private Long amount;
    private String type;
    private String reason;
    private Long orderId;
    private String orderNumber;
    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;
    private LocalDateTime expiryDate;
    private Integer expiringPoints;

    public Integer getCurrentPoints() {
        return currentPoint;
    }

    public Integer getExpiringPoints() {
        return expiringPoints;
    }
}