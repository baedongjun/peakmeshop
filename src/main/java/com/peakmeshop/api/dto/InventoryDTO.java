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
        private int quantity;
        private int reservedQuantity;
        private int availableQuantity;
        private int lowStockThreshold;
        private boolean isLowStock;
        private LocalDateTime lastUpdated;
}