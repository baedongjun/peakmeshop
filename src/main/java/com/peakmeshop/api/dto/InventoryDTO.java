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
public class InventoryDTO {
        private Long id;
        private Long productId;
        private String productName;
        private Integer quantity;
        private Integer reservedQuantity;
        private Integer lowStockThreshold;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
}