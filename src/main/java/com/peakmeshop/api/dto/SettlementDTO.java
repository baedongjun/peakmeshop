package com.peakmeshop.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementDTO {
    private Long id;
    private Long supplierId;
    private String supplierName;
    private Long totalAmount;
    private Long commissionAmount;
    private Long settlementAmount;
    private String status;
    private LocalDateTime settlementDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 