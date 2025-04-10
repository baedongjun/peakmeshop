package com.peakmeshop.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSummaryDTO {
    private long totalMembers;
    private long monthlyNewMembers;
    private long dailyNewMembers;
    private long activeMembers;
    private long inactiveMembers;
    private double averageOrdersPerMember;
    private double averageRevenuePerMember;
    private double totalPoints;
    private double averagePoints;
} 