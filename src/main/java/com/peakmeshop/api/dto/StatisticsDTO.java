package com.peakmeshop.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public record StatisticsDTO(
        Long id,
        String type,
        LocalDate date,
        double totalSales,
        int totalOrders,
        int totalProducts,
        double averageOrderValue,
        int newCustomers,
        int returningCustomers,
        double conversionRate,
        Map<String, Double> salesByCategory,
        Map<String, Double> salesByProduct,
        Map<String, Integer> ordersByStatus,
        Map<String, Double> salesByPaymentMethod,
        Map<String, Double> salesByRegion,
        Map<String, Object> additionalData
) {
    public record Product(
            Long id,
            String name,
            String category,
            BigDecimal totalSales,
            BigDecimal totalRevenue,
            int totalReviews,
            double averageRating
    ) {}

    public record Member(
            Long id,
            String email,
            String name,
            LocalDate createdAt,
            int totalOrders,
            double totalOrderAmount,
            int totalPoints,
            String status
    ) {}

    public record Traffic(
            LocalDateTime timestamp,
            int pageViews,
            int uniqueVisitors,
            double bounceRate,
            double averageSessionDuration,
            String mostVisitedPage
    ) {}

    public record Summary(
            double totalSales,
            int totalOrders,
            Long totalProducts,
            Long totalMembers,
            Long activeMembers,
            double averageOrderValue,
            double conversionRate,
            Map<String, Double> salesByCategory,
            Map<String, Integer> ordersByStatus,
            Map<String, Double> salesByPaymentMethod
    ) {}
}