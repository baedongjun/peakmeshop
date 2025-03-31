package com.peakmeshop.api.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointHistoryDTO {

    private Long id;
    private Long memberId;
    private String memberName;
    private Integer amount;
    private String type; // EARN, USE, DEDUCT, EXPIRE
    private String reason;
    private Integer balanceAfter;
    private LocalDateTime createdAt;
    private String orderId; // 주문 관련 포인트 내역인 경우
}