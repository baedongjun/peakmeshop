package com.peakmeshop.api.dto;

import java.time.LocalDateTime;

import com.peakmeshop.domain.enums.InventoryActionType;

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
    private Integer quantityBefore;
    private Integer quantityAfter;
    private Integer quantityChanged;
    private String reason;
    private InventoryActionType actionType;
    private Long memberId;
    private String memberName;
    private Long orderId;
    private LocalDateTime createdAt;
}