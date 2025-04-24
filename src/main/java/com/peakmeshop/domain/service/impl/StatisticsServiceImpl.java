package com.peakmeshop.domain.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.StatisticsDTO;
import com.peakmeshop.domain.entity.Statistics;
import com.peakmeshop.domain.enums.OrderStatus;
import com.peakmeshop.domain.repository.MemberRepository;
import com.peakmeshop.domain.repository.OrderItemRepository;
import com.peakmeshop.domain.repository.OrderRepository;
import com.peakmeshop.domain.repository.ProductRepository;
import com.peakmeshop.domain.repository.StatisticsRepository;
import com.peakmeshop.domain.service.StatisticsService;
import com.peakmeshop.api.mapper.StatisticsMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final StatisticsRepository statisticsRepository;
    private final StatisticsMapper statisticsMapper;

    @Override
    @Transactional(readOnly = true)
    public StatisticsDTO getDailyStatistics(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        double totalSales = calculateTotalSales(startOfDay, endOfDay);
        int totalOrders = calculateTotalOrders(startOfDay, endOfDay);
        int newCustomers = calculateNewCustomers(startOfDay, endOfDay);
        double conversionRate = calculateConversionRate(startOfDay, endOfDay);

        Map<String, Double> salesByCategory = calculateSalesByCategory(startOfDay, endOfDay);
        Map<String, Double> salesByProduct = calculateSalesByProduct(startOfDay, endOfDay, 10);
        Map<String, Integer> ordersByStatus = calculateOrdersByStatus(startOfDay, endOfDay);
        Map<String, Double> salesByPaymentMethod = calculateSalesByPaymentMethod(startOfDay, endOfDay);
        Map<String, Double> salesByRegion = calculateSalesByRegion(startOfDay, endOfDay);
        Map<String, Object> additionalMetrics = new HashMap<>();

        return new StatisticsDTO(
                null, // id
                "DAILY", // type
                date, // date
                totalSales,
                totalOrders,
                newCustomers,
                conversionRate,
                0, // visits
                0, // pageViews
                0.0, // averageOrderValue
                salesByCategory,
                salesByProduct,
                ordersByStatus,
                salesByPaymentMethod,
                salesByRegion,
                additionalMetrics
        );
    }

    @Override
    @Transactional(readOnly = true)
    public StatisticsDTO getWeeklyStatistics(LocalDate startDate) {
        LocalDate endDate = startDate.plusDays(6);
        return getStatisticsForDateRange(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public StatisticsDTO getMonthlyStatistics(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        return getStatisticsForDateRange(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public StatisticsDTO getYearlyStatistics(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        return getStatisticsForDateRange(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public StatisticsDTO getStatisticsForDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        double totalSales = calculateTotalSales(startDateTime, endDateTime);
        int totalOrders = calculateTotalOrders(startDateTime, endDateTime);
        int newCustomers = calculateNewCustomers(startDateTime, endDateTime);
        double conversionRate = calculateConversionRate(startDateTime, endDateTime);

        Map<String, Double> salesByCategory = calculateSalesByCategory(startDateTime, endDateTime);
        Map<String, Double> salesByProduct = calculateSalesByProduct(startDateTime, endDateTime, 10);
        Map<String, Integer> ordersByStatus = calculateOrdersByStatus(startDateTime, endDateTime);
        Map<String, Double> salesByPaymentMethod = calculateSalesByPaymentMethod(startDateTime, endDateTime);
        Map<String, Double> salesByRegion = calculateSalesByRegion(startDateTime, endDateTime);
        Map<String, Object> additionalMetrics = new HashMap<>();

        String type = "CUSTOM";
        if (startDate.plusDays(6).equals(endDate)) {
            type = "WEEKLY";
        } else if (startDate.getDayOfMonth() == 1 && endDate.equals(YearMonth.of(startDate.getYear(), startDate.getMonth()).atEndOfMonth())) {
            type = "MONTHLY";
        } else if (startDate.equals(LocalDate.of(startDate.getYear(), 1, 1)) && endDate.equals(LocalDate.of(startDate.getYear(), 12, 31))) {
            type = "YEARLY";
        }

        return new StatisticsDTO(
                null, // id
                type, // type
                startDate, // date
                totalSales,
                totalOrders,
                newCustomers,
                conversionRate,
                0, // visits
                0, // pageViews
                totalOrders > 0 ? totalSales / totalOrders : 0.0, // averageOrderValue
                salesByCategory,
                salesByProduct,
                ordersByStatus,
                salesByPaymentMethod,
                salesByRegion,
                additionalMetrics
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatisticsDTO> getDailyStatisticsForRange(LocalDate startDate, LocalDate endDate) {
        List<StatisticsDTO> result = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            result.add(getDailyStatistics(currentDate));
            currentDate = currentDate.plusDays(1);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatisticsDTO> getMonthlyStatisticsForYear(int year) {
        List<StatisticsDTO> result = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            result.add(getMonthlyStatistics(year, month));
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Double> getSalesByCategory(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return calculateSalesByCategory(startDateTime, endDateTime);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Double> getSalesByProduct(LocalDate startDate, LocalDate endDate, int limit) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return calculateSalesByProduct(startDateTime, endDateTime, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Integer> getOrdersByStatus(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return calculateOrdersByStatus(startDateTime, endDateTime);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Double> getSalesByPaymentMethod(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return calculateSalesByPaymentMethod(startDateTime, endDateTime);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Double> getSalesByRegion(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return calculateSalesByRegion(startDateTime, endDateTime);
    }

    @Override
    @Transactional(readOnly = true)
    public double getTotalSales(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return calculateTotalSales(startDateTime, endDateTime);
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalOrders(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return calculateTotalOrders(startDateTime, endDateTime);
    }

    @Override
    @Transactional(readOnly = true)
    public int getNewCustomers(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return calculateNewCustomers(startDateTime, endDateTime);
    }

    @Override
    @Transactional(readOnly = true)
    public double getConversionRate(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return calculateConversionRate(startDateTime, endDateTime);
    }

    @Override
    @Transactional
    public void generateDailyStatistics() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        StatisticsDTO statistics = getDailyStatistics(yesterday);
        
        Statistics entity = Statistics.builder()
                .type("DAILY")
                .date(yesterday)
                .totalSales(statistics.totalSales())
                .totalOrders(statistics.totalOrders())
                .newCustomers(statistics.newCustomers())
                .conversionRate(statistics.conversionRate())
                .salesByCategory(statistics.salesByCategory())
                .salesByProduct(statistics.salesByProduct())
                .ordersByStatus(statistics.ordersByStatus())
                .salesByPaymentMethod(statistics.salesByPaymentMethod())
                .salesByRegion(statistics.salesByRegion())
                .build();
                
        statisticsRepository.save(entity);
    }

    @Override
    @Transactional
    public void generateMonthlyStatistics() {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        StatisticsDTO statistics = getMonthlyStatistics(lastMonth.getYear(), lastMonth.getMonthValue());
        
        Statistics entity = Statistics.builder()
                .type("MONTHLY")
                .date(lastMonth.withDayOfMonth(1))
                .totalSales(statistics.totalSales())
                .totalOrders(statistics.totalOrders())
                .newCustomers(statistics.newCustomers())
                .conversionRate(statistics.conversionRate())
                .salesByCategory(statistics.salesByCategory())
                .salesByProduct(statistics.salesByProduct())
                .ordersByStatus(statistics.ordersByStatus())
                .salesByPaymentMethod(statistics.salesByPaymentMethod())
                .salesByRegion(statistics.salesByRegion())
                .build();
                
        statisticsRepository.save(entity);
    }

    @Override
    @Transactional
    public void generateYearlyStatistics() {
        int lastYear = LocalDate.now().getYear() - 1;
        StatisticsDTO statistics = getYearlyStatistics(lastYear);
        
        Statistics entity = Statistics.builder()
                .type("YEARLY")
                .date(LocalDate.of(lastYear, 1, 1))
                .totalSales(statistics.totalSales())
                .totalOrders(statistics.totalOrders())
                .newCustomers(statistics.newCustomers())
                .conversionRate(statistics.conversionRate())
                .salesByCategory(statistics.salesByCategory())
                .salesByProduct(statistics.salesByProduct())
                .ordersByStatus(statistics.ordersByStatus())
                .salesByPaymentMethod(statistics.salesByPaymentMethod())
                .salesByRegion(statistics.salesByRegion())
                .build();
                
        statisticsRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatisticsDTO.Product> getProductStatistics(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        return productRepository.findTopProducts(startDateTime, endDateTime)
                .stream()
                .map(product -> new StatisticsDTO.Product(
                        product.getId(),
                        product.getName(),
                        product.getCategory().getName(),
                        product.getTotalSales(),
                        product.getTotalRevenue(),
                        product.getReviewCount(),
                        product.getAverageRating()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatisticsDTO.Member> getMemberStatistics(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        return memberRepository.findActiveMembers(startDateTime, endDateTime)
                .stream()
                .map(member -> new StatisticsDTO.Member(
                        member.id(),
                        member.email(),
                        member.name(),
                        member.createdAt(),
                        member.totalOrders(),
                        member.totalOrderAmount(),
                        member.totalPoints(),
                        member.status()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatisticsDTO.Traffic> getTrafficStatistics(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // TODO: 실제 트래픽 데이터를 가져오는 로직 구현 필요
        // 현재는 임시 데이터 반환
        List<StatisticsDTO.Traffic> trafficList = new ArrayList<>();
        
        LocalDateTime current = startDateTime;
        while (!current.isAfter(endDateTime)) {
            trafficList.add(new StatisticsDTO.Traffic(
                    current,
                    100, // pageViews
                    50,  // uniqueVisitors
                    30.0, // bounceRate
                    120.0, // averageSessionDuration
                    "/products" // mostVisitedPage
            ));
            current = current.plusHours(1);
        }
        
        return trafficList;
    }

    @Override
    @Transactional(readOnly = true)
    public StatisticsDTO.Summary getSummaryStatistics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = now.withDayOfMonth(now.toLocalDate().lengthOfMonth())
                .withHour(23).withMinute(59).withSecond(59);

        double totalSales = calculateTotalSales(startOfMonth, endOfMonth);
        int totalOrders = calculateTotalOrders(startOfMonth, endOfMonth);
        Long totalProducts = productRepository.count();
        Long totalMembers = memberRepository.count();
        Long activeMembers = memberRepository.countByStatus("ACTIVE");
        double averageOrderValue = totalOrders > 0 ? totalSales / totalOrders : 0.0;
        double conversionRate = calculateConversionRate(startOfMonth, endOfMonth);

        Map<String, Double> salesByCategory = calculateSalesByCategory(startOfMonth, endOfMonth);
        Map<String, Integer> ordersByStatus = calculateOrdersByStatus(startOfMonth, endOfMonth);
        Map<String, Double> salesByPaymentMethod = calculateSalesByPaymentMethod(startOfMonth, endOfMonth);

        return new StatisticsDTO.Summary(
                totalSales,
                totalOrders,
                totalProducts,
                totalMembers,
                activeMembers,
                averageOrderValue,
                conversionRate,
                salesByCategory,
                ordersByStatus,
                salesByPaymentMethod
        );
    }

    // 헬퍼 메서드들
    private double calculateTotalSales(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return orderRepository.findByCreatedAtBetweenAndStatusNot(
                startDateTime, 
                endDateTime, 
                OrderStatus.CANCELLED
            )
            .stream()
            .mapToDouble(order -> order.getTotalAmount())
            .sum();
    }

    private int calculateTotalOrders(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return (int) orderRepository.countByCreatedAtBetween(startDateTime, endDateTime);
    }

    private int calculateNewCustomers(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return (int) memberRepository.countByCreatedAtBetween(startDateTime, endDateTime);
    }

    private double calculateConversionRate(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        int orders = calculateTotalOrders(startDateTime, endDateTime);
        int visits = getVisitCount(startDateTime, endDateTime);
        return visits > 0 ? (double) orders / visits * 100 : 0.0;
    }

    private int getVisitCount(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // TODO: 실제 방문자 수를 가져오는 로직 구현
        // 예: Google Analytics API 또는 자체 로깅 시스템에서 데이터 조회
        return 100; // 임시 값
    }

    private Map<String, Double> calculateSalesByCategory(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return orderItemRepository.findByOrder_CreatedAtBetweenAndOrder_StatusCompleted(startDateTime, endDateTime)
                .stream()
                .collect(Collectors.groupingBy(
                        item -> item.getProduct().getCategory().getName(),
                        Collectors.summingDouble(item -> 
                            item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())).doubleValue())
                ));
    }

    private Map<String, Double> calculateSalesByProduct(LocalDateTime startDateTime, LocalDateTime endDateTime, int limit) {
        return orderItemRepository.findByOrder_CreatedAtBetweenAndOrder_StatusCompleted(startDateTime, endDateTime)
                .stream()
                .collect(Collectors.groupingBy(
                        item -> item.getProduct().getName(),
                        Collectors.summingDouble(item -> 
                            item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())).doubleValue())
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private Map<String, Integer> calculateOrdersByStatus(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return orderRepository.findAllByCreatedAtBetween(startDateTime, endDateTime)
                .stream()
                .collect(Collectors.groupingBy(
                        order -> order.getStatus().name(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
    }

    private Map<String, Double> calculateSalesByPaymentMethod(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return orderRepository.findAllByCreatedAtBetween(startDateTime, endDateTime)
                .stream()
                .collect(Collectors.groupingBy(
                        order -> order.getPaymentMethod().name(),
                        Collectors.summingDouble(order -> order.getTotalAmount())
                ));
    }

    private Map<String, Double> calculateSalesByRegion(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return orderRepository.findAllByCreatedAtBetween(startDateTime, endDateTime)
                .stream()
                .collect(Collectors.groupingBy(
                        order -> order.getRecipientAddress().split(" ")[0], // 첫 번째 행정구역을 기준으로 집계
                        Collectors.summingDouble(order -> order.getTotalAmount())
                ));
    }
}