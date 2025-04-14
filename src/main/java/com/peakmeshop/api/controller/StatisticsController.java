package com.peakmeshop.api.controller;

import com.peakmeshop.api.dto.StatisticsDTO;
import com.peakmeshop.domain.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/daily")
    public ResponseEntity<StatisticsDTO> getDailyStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(statisticsService.getDailyStatistics(date));
    }

    @GetMapping("/weekly")
    public ResponseEntity<StatisticsDTO> getWeeklyStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        return ResponseEntity.ok(statisticsService.getWeeklyStatistics(startDate));
    }

    @GetMapping("/monthly")
    public ResponseEntity<StatisticsDTO> getMonthlyStatistics(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(statisticsService.getMonthlyStatistics(year, month));
    }

    @GetMapping("/yearly")
    public ResponseEntity<StatisticsDTO> getYearlyStatistics(
            @RequestParam int year) {
        return ResponseEntity.ok(statisticsService.getYearlyStatistics(year));
    }

    @GetMapping("/range")
    public ResponseEntity<StatisticsDTO> getStatisticsForDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticsService.getStatisticsForDateRange(startDate, endDate));
    }

    @GetMapping("/sales")
    public ResponseEntity<List<StatisticsDTO>> getSalesStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticsService.getDailyStatisticsForRange(startDate, endDate));
    }

    @GetMapping("/products")
    public ResponseEntity<List<StatisticsDTO.Product>> getProductStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticsService.getProductStatistics(startDate, endDate));
    }

    @GetMapping("/members")
    public ResponseEntity<List<StatisticsDTO.Member>> getMemberStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticsService.getMemberStatistics(startDate, endDate));
    }

    @GetMapping("/traffic")
    public ResponseEntity<List<StatisticsDTO.Traffic>> getTrafficStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime) {
        return ResponseEntity.ok(statisticsService.getTrafficStatistics(startDateTime, endDateTime));
    }

    @GetMapping("/summary")
    public ResponseEntity<StatisticsDTO.Summary> getSummaryStatistics() {
        return ResponseEntity.ok(statisticsService.getSummaryStatistics());
    }
} 