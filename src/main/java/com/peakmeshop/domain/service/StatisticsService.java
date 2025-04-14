package com.peakmeshop.domain.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.peakmeshop.api.dto.StatisticsDTO;

public interface StatisticsService {

    StatisticsDTO getDailyStatistics(LocalDate date);

    StatisticsDTO getWeeklyStatistics(LocalDate startDate);

    StatisticsDTO getMonthlyStatistics(int year, int month);

    StatisticsDTO getYearlyStatistics(int year);

    StatisticsDTO getStatisticsForDateRange(LocalDate startDate, LocalDate endDate);

    List<StatisticsDTO> getDailyStatisticsForRange(LocalDate startDate, LocalDate endDate);

    List<StatisticsDTO> getMonthlyStatisticsForYear(int year);

    Map<String, Double> getSalesByCategory(LocalDate startDate, LocalDate endDate);

    Map<String, Double> getSalesByProduct(LocalDate startDate, LocalDate endDate, int limit);

    Map<String, Integer> getOrdersByStatus(LocalDate startDate, LocalDate endDate);

    Map<String, Double> getSalesByPaymentMethod(LocalDate startDate, LocalDate endDate);

    Map<String, Double> getSalesByRegion(LocalDate startDate, LocalDate endDate);

    double getTotalSales(LocalDate startDate, LocalDate endDate);

    int getTotalOrders(LocalDate startDate, LocalDate endDate);

    int getNewCustomers(LocalDate startDate, LocalDate endDate);

    double getConversionRate(LocalDate startDate, LocalDate endDate);

    void generateDailyStatistics();

    void generateMonthlyStatistics();

    void generateYearlyStatistics();

    List<StatisticsDTO.Product> getProductStatistics(LocalDate startDate, LocalDate endDate);

    List<StatisticsDTO.Member> getMemberStatistics(LocalDate startDate, LocalDate endDate);

    List<StatisticsDTO.Traffic> getTrafficStatistics(LocalDateTime startDateTime, LocalDateTime endDateTime);

    StatisticsDTO.Summary getSummaryStatistics();
}