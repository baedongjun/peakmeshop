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
    private String conditionType;
    private int conditionValue;
    private String benefitType;
    private int benefitValue;
    private boolean isFreeShipping;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 