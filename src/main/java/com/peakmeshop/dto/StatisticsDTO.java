package com.peakmeshop.dto;

import java.time.LocalDate;
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
) {}