package com.peakmeshop.api.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryHistoryDTO {
    private Long id;
    private Long productId;
    private String productName;
    private int quantityBefore;
    private int quantityAfter;
    private int quantityChanged;
    private String reason;
    private String actionType; // INCREASE, DECREASE, RESERVE, RELEASE, CONFIRM
    private String userId;
    private String userName;
    private Long orderId;
    private LocalDateTime createdAt;
}