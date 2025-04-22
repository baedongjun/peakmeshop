package com.peakmeshop.domain.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.StatisticsDTO;
import com.peakmeshop.domain.repository.MemberRepository;
import com.peakmeshop.domain.repository.OrderItemRepository;
import com.peakmeshop.domain.repository.OrderRepository;
import com.peakmeshop.domain.repository.ProductRepository;
import com.peakmeshop.domain.service.StatisticsService;

@Service
@Transactional
public class StatisticsServiceImpl implements StatisticsService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    public StatisticsServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            MemberRepository memberRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.memberRepository = memberRepository;
    }

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
        // 일별 통계 생성 로직 구현
        // 예: 어제 날짜의 통계를 생성하고 저장
        LocalDate yesterday = LocalDate.now().minusDays(1);
        StatisticsDTO statistics = getDailyStatistics(yesterday);
        // 통계 저장 로직 (필요시 구현)
    }

    @Override
    @Transactional
    public void generateMonthlyStatistics() {
        // 월별 통계 생성 로직 구현
        // 예: 지난 달의 통계를 생성하고 저장
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        StatisticsDTO statistics = getMonthlyStatistics(lastMonth.getYear(), lastMonth.getMonthValue());
        // 통계 저장 로직 (필요시 구현)
    }

    @Override
    @Transactional
    public void generateYearlyStatistics() {
        // 연간 통계 생성 로직 구현
        // 예: 지난 해의 통계를 생성하고 저장
        int lastYear = LocalDate.now().getYear() - 1;
        StatisticsDTO statistics = getYearlyStatistics(lastYear);
        // 통계 저장 로직 (필요시 구현)
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
        // OrderRepository에 해당 메서드 추가 필요
        // return orderRepository.calculateSalesAmountBetween(startDateTime, endDateTime);

        // 임시 구현 (실제로는 OrderRepository에 적절한 메서드 추가 필요)
        return 0.0;
    }

    private int calculateTotalOrders(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // OrderRepository에 해당 메서드 추가 필요
        // return orderRepository.countByCreatedAtBetween(startDateTime, endDateTime);

        // 임시 구현 (실제로는 OrderRepository에 적절한 메서드 추가 필요)
        return 0;
    }

    private int calculateNewCustomers(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // MemberRepository에 해당 메서드 추가 필요
        // return memberRepository.countByCreatedAtBetween(startDateTime, endDateTime);

        // 임시 구현 (실제로는 MemberRepository에 적절한 메서드 추가 필요)
        return 0;
    }

    private double calculateConversionRate(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // 전환율 계산 로직 구현
        // 예: 주문 수 / 방문자 수 * 100
        int orders = calculateTotalOrders(startDateTime, endDateTime);
        int visits = 0; // 방문자 수를 가져오는 로직 필요

        return visits > 0 ? (double) orders / visits * 100 : 0.0;
    }

    private Map<String, Double> calculateSalesByCategory(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // 카테고리별 매출 계산 로직 구현
        // OrderRepository 또는 OrderItemRepository에 해당 메서드 추가 필요

        // 임시 구현 (실제로는 적절한 리포지토리 메서드 추가 필요)
        return new HashMap<>();
    }

    private Map<String, Double> calculateSalesByProduct(LocalDateTime startDateTime, LocalDateTime endDateTime, int limit) {
        // 상품별 매출 계산 로직 구현
        // OrderItemRepository에 해당 메서드 추가 필요

        // 임시 구현 (실제로는 적절한 리포지토리 메서드 추가 필요)
        return new HashMap<>();
    }

    private Map<String, Integer> calculateOrdersByStatus(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // 주문 상태별 주문 수 계산 로직 구현
        // OrderRepository에 해당 메서드 추가 필요

        // 임시 구현 (실제로는 적절한 리포지토리 메서드 추가 필요)
        return new HashMap<>();
    }

    private Map<String, Double> calculateSalesByPaymentMethod(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // 결제 방법별 매출 계산 로직 구현
        // OrderRepository에 해당 메서드 추가 필요

        // 임시 구현 (실제로는 적절한 리포지토리 메서드 추가 필요)
        return new HashMap<>();
    }

    private Map<String, Double> calculateSalesByRegion(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // 지역별 매출 계산 로직 구현
        // OrderRepository에 해당 메서드 추가 필요

        // 임시 구현 (실제로는 적절한 리포지토리 메서드 추가 필요)
        return new HashMap<>();
    }
}