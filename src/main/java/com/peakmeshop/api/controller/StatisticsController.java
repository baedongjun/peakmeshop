package com.peakmeshop.api.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peakmeshop.api.dto.StatisticsDTO;
import com.peakmeshop.domain.service.StatisticsService;

@RestController
@RequestMapping("/api/statistics")
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/daily/{date}")
    public ResponseEntity<StatisticsDTO> getDailyStatistics(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(statisticsService.getDailyStatistics(date));
    }

    @GetMapping("/weekly/{startDate}")
    public ResponseEntity<StatisticsDTO> getWeeklyStatistics(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        return ResponseEntity.ok(statisticsService.getWeeklyStatistics(startDate));
    }

    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<StatisticsDTO> getMonthlyStatistics(
            @PathVariable int year,
            @PathVariable int month) {
        return ResponseEntity.ok(statisticsService.getMonthlyStatistics(year, month));
    }

    @GetMapping("/yearly/{year}")
    public ResponseEntity<StatisticsDTO> getYearlyStatistics(@PathVariable int year) {
        return ResponseEntity.ok(statisticsService.getYearlyStatistics(year));
    }

    @GetMapping("/range")
    public ResponseEntity<StatisticsDTO> getStatisticsForDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticsService.getStatisticsForDateRange(startDate, endDate));
    }

    @GetMapping("/daily/range")
    public ResponseEntity<List<StatisticsDTO>> getDailyStatisticsForRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticsService.getDailyStatisticsForRange(startDate, endDate));
    }

    @GetMapping("/monthly/year/{year}")
    public ResponseEntity<List<StatisticsDTO>> getMonthlyStatisticsForYear(@PathVariable int year) {
        return ResponseEntity.ok(statisticsService.getMonthlyStatisticsForYear(year));
    }

    @GetMapping("/sales/category")
    public ResponseEntity<Map<String, Double>> getSalesByCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticsService.getSalesByCategory(startDate, endDate));
    }

    @GetMapping("/sales/product")
    public ResponseEntity<Map<String, Double>> getSalesByProduct(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(statisticsService.getSalesByProduct(startDate, endDate, limit));
    }

    @GetMapping("/orders/status")
    public ResponseEntity<Map<String, Integer>> getOrdersByStatus(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticsService.getOrdersByStatus(startDate, endDate));
    }

    @GetMapping("/sales/payment-method")
    public ResponseEntity<Map<String, Double>> getSalesByPaymentMethod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticsService.getSalesByPaymentMethod(startDate, endDate));
    }

    @GetMapping("/sales/region")
    public ResponseEntity<Map<String, Double>> getSalesByRegion(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticsService.getSalesByRegion(startDate, endDate));
    }

    @GetMapping("/sales/total")
    public ResponseEntity<Double> getTotalSales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticsService.getTotalSales(startDate, endDate));
    }

    @GetMapping("/orders/total")
    public ResponseEntity<Integer> getTotalOrders(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticsService.getTotalOrders(startDate, endDate));
    }

    @GetMapping("/customers/new")
    public ResponseEntity<Integer> getNewCustomers(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticsService.getNewCustomers(startDate, endDate));
    }

    @GetMapping("/conversion-rate")
    public ResponseEntity<Double> getConversionRate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticsService.getConversionRate(startDate, endDate));
    }

    @PostMapping("/generate/daily")
    public ResponseEntity<Void> generateDailyStatistics() {
        statisticsService.generateDailyStatistics();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/generate/monthly")
    public ResponseEntity<Void> generateMonthlyStatistics() {
        statisticsService.generateMonthlyStatistics();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/generate/yearly")
    public ResponseEntity<Void> generateYearlyStatistics() {
        statisticsService.generateYearlyStatistics();
        return ResponseEntity.ok().build();
    }
}