package com.peakmeshop.dto;

import java.util.Map;

/**
 * 리뷰 요약 정보를 전송하기 위한 DTO
 */
public record ReviewSummaryDTO(
        Long productId,
        double averageRating,
        int totalReviews,
        Map<Integer, Integer> ratingDistribution, // 평점별 리뷰 수
        int recommendedCount,
        int verifiedCount
) {}