package com.peakmeshop.service;

import java.time.LocalDate;

import com.peakmeshop.dto.StatisticsDTO;

public interface StatisticsService {

    /**
     * 대시보드 통계 조회
     * @return 통계 정보
     */
    StatisticsDTO getDashboardStatistics();

    /**
     * 기간별 매출 통계 조회
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 매출 통계
     */
    java.util.Map<String, java.math.BigDecimal> getSalesByPeriod(LocalDate startDate, LocalDate endDate);

    /**
     * 카테고리별 매출 통계 조회
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 카테고리별 매출 통계
     */
    java.util.List<CategorySalesDTO> getSalesByCategory(LocalDate startDate, LocalDate endDate);

    /**
     * 인기 상품 조회
     * @param limit 조회 개수
     * @return 인기 상품 목록
     */
    java.util.List<ProductSummaryDTO> getTopSellingProducts(int limit);

    /**
     * 신규 회원 통계 조회
     * @return 신규 회원 통계
     */
    java.util.Map<String, Integer> getNewMembersStatistics();

    /**
     * 주문 상태별 통계 조회
     * @return 주문 상태별 통계
     */
    java.util.Map<String, Integer> getOrdersByStatus();
}