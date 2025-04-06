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
public class MemberGradeDTO {
    private Long id;
    private String name;
    private String description;
    private Integer level;
    private Double discountRate;
    private Integer pointRate;
    private Long minAmount;
    private Long maxAmount;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 