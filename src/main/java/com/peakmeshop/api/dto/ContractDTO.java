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
public class ContractDTO {
    private Long id;
    private Long supplierId;
    private String supplierName;
    private String contractType;
    private Double commissionRate;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 