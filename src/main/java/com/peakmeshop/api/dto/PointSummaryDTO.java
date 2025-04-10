package com.peakmeshop.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointSummaryDTO {
    private long totalPoints;              // 전체 포인트
    private long totalEarnedPoints;        // 총 적립 포인트
    private long totalUsedPoints;          // 총 사용 포인트
    private long totalExpiredPoints;       // 총 만료 포인트
    private long monthlyEarnedPoints;      // 월간 적립 포인트
    private long monthlyUsedPoints;        // 월간 사용 포인트
    private long dailyEarnedPoints;        // 일간 적립 포인트
    private long dailyUsedPoints;          // 일간 사용 포인트
    private double averagePointsPerMember; // 회원당 평균 포인트
    private List<Map<String, Object>> pointsByType;      // 유형별 포인트 통계
    private List<Map<String, Object>> dailyStatistics;   // 일별 포인트 통계
    private List<Map<String, Object>> monthlyStatistics; // 월별 포인트 통계
} 