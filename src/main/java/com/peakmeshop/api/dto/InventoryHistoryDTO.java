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
    private Long inventoryId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private String reason;
    private Long userId;
    private String userName;
    private LocalDateTime createdAt;
}