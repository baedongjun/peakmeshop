package com.peakmeshop.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionSummaryDTO {
    private long totalPromotions;
    private long activePromotions;
    private long monthlyPromotions;
    private long totalDiscountAmount;
    private long totalParticipants;
} 