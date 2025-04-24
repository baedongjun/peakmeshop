package com.peakmeshop.domain.service.impl;

import com.peakmeshop.api.dto.*;
import com.peakmeshop.api.mapper.*;
import com.peakmeshop.domain.entity.*;
import com.peakmeshop.domain.enums.OrderStatus;
import com.peakmeshop.domain.repository.*;
import com.peakmeshop.domain.service.EmailService;
import com.peakmeshop.domain.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final EmailService emailService;
    private final Random random = new Random();

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final MemberMapper memberMapper;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));
        return orderMapper.toDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByMemberId(Long memberId) {
        return orderRepository.findByMemberIdOrderByCreatedAtDesc(memberId).stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByMemberId(Long memberId, Pageable pageable) {
        return orderRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable)
                .map(orderMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable)
                .map(orderMapper::toDTO);
    }

    @Override
    @Transactional
    public OrderDTO createOrder(Long memberId, OrderRequestDTO orderRequestDTO) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));

        Order order = Order.builder()
                .member(member)
                .orderNumber(generateOrderNumber())
                .status(OrderStatus.PENDING)
                .totalPrice(orderRequestDTO.getTotalAmount())
                .discount(orderRequestDTO.getDiscountAmount())
                .deliveryFee(orderRequestDTO.getShippingCost())
                .finalPrice(orderRequestDTO.getTotalAmount().doubleValue() - orderRequestDTO.getDiscountAmount().doubleValue() + orderRequestDTO.getShippingCost().doubleValue())
                .recipientName(orderRequestDTO.getRecipientName())
                .recipientTel(orderRequestDTO.getRecipientTel())
                .recipientAddress(orderRequestDTO.getRecipientAddress())
                .recipientDetailAddress(orderRequestDTO.getRecipientDetailAddress())
                .recipientMessage(orderRequestDTO.getRecipientMessage())
                .paymentMethod(orderRequestDTO.getPaymentMethod())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        order = orderRepository.save(order);

        for (OrderItemDTO itemDTO : orderRequestDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

            if (product.getStock() < itemDTO.getQuantity()) {
                throw new IllegalArgumentException("상품의 재고가 부족합니다: " + product.getName());
            }

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .price(product.getPrice())
                    .quantity(itemDTO.getQuantity())
                    .build();

            orderItemRepository.save(orderItem);

            product.setStock(product.getStock() - itemDTO.getQuantity());
            productRepository.save(product);
        }

        return orderMapper.toDTO(order);
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long id, OrderStatusUpdateDTO statusUpdateDTO) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));

        order.setStatus(statusUpdateDTO.getStatus());
        order.setUpdatedAt(LocalDateTime.now());

        if (statusUpdateDTO.getTrackingNumber() != null) {
            order.setTrackingNumber(statusUpdateDTO.getTrackingNumber());
        }
        if (statusUpdateDTO.getShippingCompany() != null) {
            order.setShippingCompany(statusUpdateDTO.getShippingCompany());
        }

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDTO(savedOrder);
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDTO(savedOrder);
    }

    @Override
    @Transactional
    public OrderDTO updateTrackingInfo(Long id, String trackingNumber, String shippingCompany) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));

        order.setTrackingNumber(trackingNumber);
        order.setShippingCompany(shippingCompany);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDTO(savedOrder);
    }

    @Override
    @Transactional
    public OrderDTO refundOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));

        order.setStatus(OrderStatus.REFUNDED);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDTO(savedOrder);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getCancelledOrders(Pageable pageable) {
        Page<Order> orders = orderRepository.findByStatusOrderByCreatedAtDesc(OrderStatus.CANCELLED, pageable);
        return orders.map(orderMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemDTO> getOrderItems(Long orderId) {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return items.stream()
                .map(orderItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Scheduled(cron = "0 0 1 * * ?") // 매일 새벽 1시에 실행
    @Transactional
    public void cancelAbandonedOrders() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        List<Order> abandonedOrders = orderRepository.findByStatusAndCreatedAtBefore(
                OrderStatus.PENDING, cutoffTime);

        for (Order order : abandonedOrders) {
            try {
                OrderStatusUpdateDTO statusUpdateDTO = new OrderStatusUpdateDTO();
                statusUpdateDTO.setStatus(OrderStatus.CANCELLED);
                statusUpdateDTO.setReason("자동 취소: 24시간 이내 결제 미완료");

                updateOrderStatus(order.getId(), statusUpdateDTO);
                log.info("Abandoned order automatically cancelled: {}", order.getOrderNumber());
            } catch (Exception e) {
                log.error("Failed to cancel abandoned order: {}", order.getOrderNumber(), e);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getOrderCountByStatus() {
        Map<String, Long> result = new HashMap<>();

        result.put("PENDING", orderRepository.countByStatus(OrderStatus.PENDING));
        result.put("PAID", orderRepository.countByStatus(OrderStatus.COMPLETED));
        result.put("PROCESSING", orderRepository.countByStatus(OrderStatus.PROCESSING));
        result.put("SHIPPED", orderRepository.countByStatus(OrderStatus.SHIPPED));
        result.put("DELIVERED", orderRepository.countByStatus(OrderStatus.DELIVERED));
        result.put("CANCELLED", orderRepository.countByStatus(OrderStatus.CANCELLED));
        result.put("REFUNDED", orderRepository.countByStatus(OrderStatus.REFUNDED));
        result.put("TOTAL", orderRepository.count());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getRecentOrders(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orders = orderRepository.findAll(pageable);

        return orders.getContent().stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> findByMemberId(Long memberId, Pageable pageable) {
        return orderRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> findByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> findByMemberIdAndStatus(Long memberId, OrderStatus status, Pageable pageable) {
        return orderRepository.findByMemberIdAndStatus(memberId, status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByDateRange(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByMemberIdAndDateRange(Long memberId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByMemberIdAndDateRange(memberId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<OrderStatus, Long> getOrderStatusDistribution() {
        return orderRepository.getOrderStatusDistribution();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getDailySales(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.getDailySales(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getMonthlySales(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.getMonthlySales(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getTopCustomers(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return orderRepository.findTopCustomers(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getOrderStatistics(String period, String startDate, String endDate) {
        Map<String, Object> statistics = new HashMap<>();
        
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : null;
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : null;

        statistics.put("orderCount", orderRepository.countByCreatedAtBetween(start, end));
        statistics.put("statusDistribution", getOrderStatusDistribution());
        statistics.put("dailySales", getDailySales(start, end));
        statistics.put("monthlySales", getMonthlySales(start, end));

        // Convert entities to DTOs using mappers
        List<Order> recentOrders = orderRepository.findTop5ByOrderByCreatedAtDesc();
        statistics.put("recentOrders", recentOrders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList()));
                
        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSalesStatistics(String period, String startDate, String endDate) {
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : null;
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : null;

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("dailySales", getDailySales(start, end));
        statistics.put("monthlySales", getMonthlySales(start, end));
        statistics.put("topCustomers", getTopCustomers(10));

        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getOrderSummary() {
        Map<String, Long> summary = new HashMap<>();
        summary.put("totalOrders", orderRepository.count());
        summary.put("pendingOrders", orderRepository.countByStatus(OrderStatus.PENDING));
        summary.put("completedOrders", orderRepository.countByStatus(OrderStatus.DELIVERED));
        summary.put("cancelledOrders", orderRepository.countByStatus(OrderStatus.CANCELLED));
        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RefundDTO> getRefunds(Pageable pageable) {
        Page<Order> refunds = orderRepository.findByStatusOrderByCreatedAtDesc(OrderStatus.REFUNDED, pageable);
        return refunds.map(this::convertToRefundDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public RefundDTO getRefundById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("환불 정보를 찾을 수 없습니다."));
        return convertToRefundDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CancellationDTO> getCancellations(Pageable pageable) {
        Page<Order> cancellations = orderRepository.findByStatusOrderByCreatedAtDesc(OrderStatus.CANCELLED, pageable);
        return cancellations.map(this::convertToCancellationDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public CancellationDTO getCancellationById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("취소 정보를 찾을 수 없습니다."));
        return convertToCancellationDTO(order);
    }

    private RefundDTO convertToRefundDTO(Order order) {
        return RefundDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .refundNumber(generateRefundNumber())
                .memberId(order.getMember().getId())
                .memberName(order.getMember().getName())
                .amount(new BigDecimal(order.getFinalPrice()))
                .reason(order.getRefundReason())
                .refundedAt(order.getRefundedAt())
                .build();
    }

    private CancellationDTO convertToCancellationDTO(Order order) {
        return CancellationDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .memberId(order.getMember().getId())
                .memberName(order.getMember().getName())
                .amount(new BigDecimal(order.getFinalPrice()))
                .reason(order.getCancelReason())
                .cancelledAt(order.getCancelledAt())
                .build();
    }

    @Override
    public OrderSummaryDTO getOrderSummary(String period, String startDate, String endDate) {
        LocalDateTime start = null;
        LocalDateTime end = null;

        // 기간 설정
        if (startDate != null && endDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            start = LocalDate.parse(startDate, formatter).atStartOfDay();
            end = LocalDate.parse(endDate, formatter).plusDays(1).atStartOfDay();
        } else {
            LocalDate now = LocalDate.now();
            if (period != null) {
                switch (period) {
                    case "daily":
                        start = now.atStartOfDay();
                        end = now.plusDays(1).atStartOfDay();
                        break;
                    case "weekly":
                        start = now.minusWeeks(1).atStartOfDay();
                        end = now.plusDays(1).atStartOfDay();
                        break;
                    case "monthly":
                        start = now.minusMonths(1).atStartOfDay();
                        end = now.plusDays(1).atStartOfDay();
                        break;
                    case "yearly":
                        start = now.minusYears(1).atStartOfDay();
                        end = now.plusDays(1).atStartOfDay();
                        break;
                    default:
                        start = now.minusMonths(1).atStartOfDay();
                        end = now.plusDays(1).atStartOfDay();
                }
            } else {
                // period가 null인 경우에 대한 기본 케이스 처리
                start = now.minusMonths(1).atStartOfDay();
                end = now.plusDays(1).atStartOfDay();
            }
        }

        // 주문 데이터 조회
        List<Order> orders = orderRepository.findByDateRange(start, end);
        
        // 일별/월별 매출 데이터
        List<Object[]> dailySales = orderRepository.getDailySales(start, end);
        List<Object[]> monthlySales = orderRepository.getMonthlySales(start, end);

        // 상태별 주문 수
        Map<OrderStatus, Long> statusCounts = orderRepository.getOrderStatusDistribution();

        return OrderSummaryDTO.builder()
                .totalOrders((long) orders.size())
                .monthlyOrders(orders.stream()
                        .filter(o -> o.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(1)))
                        .count())
                .dailyOrders(orders.stream()
                        .filter(o -> o.getCreatedAt().isAfter(LocalDateTime.now().minusDays(1)))
                        .count())
                .totalRevenue(orders.stream()
                        .mapToDouble(Order::getTotalAmount)
                        .sum())
                .monthlyRevenue(orders.stream()
                        .filter(o -> o.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(1)))
                        .mapToDouble(Order::getTotalAmount)
                        .sum())
                .dailyRevenue(orders.stream()
                        .filter(o -> o.getCreatedAt().isAfter(LocalDateTime.now().minusDays(1)))
                        .mapToDouble(Order::getTotalAmount)
                        .sum())
                .averageOrderValue(orders.isEmpty() ? 0.0 : orders.stream()
                        .mapToDouble(order -> order.getTotalAmount().doubleValue())
                        .average()
                        .orElse(0.0))
                .pendingOrders(statusCounts.getOrDefault(OrderStatus.PENDING, 0L))
                .processingOrders(statusCounts.getOrDefault(OrderStatus.PROCESSING, 0L))
                .completedOrders(statusCounts.getOrDefault(OrderStatus.COMPLETED, 0L))
                .cancelledOrders(statusCounts.getOrDefault(OrderStatus.CANCELLED, 0L))
                .build();
    }

    @Override
    @Transactional
    public OrderDTO completeOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));
        
        // 주문 상태를 완료로 변경
        order.updateStatus(OrderStatus.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());
        
        // 회원 통계 업데이트
        Member member = order.getMember();
        if (member != null) {
            // 주문 횟수 증가
            member.updateOrderCount(member.getOrderCount() + 1);
            
            // 총 주문 건수 증가
            member.updateTotalOrders(member.getTotalOrders() + 1);
            
            // 총 구매 금액 업데이트
            member.updateTotalOrderAmount(member.getTotalOrderAmount() + order.getTotalAmount());
            
            // 포인트 적립 (구매 금액의 1%)
            double points = order.getTotalAmount() * 0.01;
            member.updateTotalPoints(member.getTotalPoints() + points);
            
            memberRepository.save(member);
        }
        
        return orderMapper.toDTO(orderRepository.save(order));
    }

    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis();
    }

    private String generateRefundNumber() {
        return "REF" + System.currentTimeMillis();
    }
}