package com.peakmeshop.domain.service;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.Optional;

import com.peakmeshop.api.dto.OrderStatusUpdateDTO;
import com.peakmeshop.domain.entity.Order;
import com.peakmeshop.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.OrderDTO;
import com.peakmeshop.api.dto.OrderItemDTO;
import com.peakmeshop.api.dto.OrderRequestDTO;
import com.peakmeshop.api.dto.RefundDTO;
import com.peakmeshop.api.dto.CancellationDTO;

public interface OrderService {

    Order getOrderById(Long id);

    OrderDTO getOrderByOrderNumber(String orderNumber);

    List<OrderDTO> getOrdersByMemberId(Long memberId);

    Page<OrderDTO> getOrdersByMemberId(Long memberId, Pageable pageable);

    Page<OrderDTO> getAllOrders(Pageable pageable);

    Page<OrderDTO> getOrdersByStatus(OrderStatus status, Pageable pageable);

    OrderDTO createOrder(Long memberId, OrderRequestDTO orderRequestDTO);

    OrderDTO updateOrderStatus(Long id, OrderStatusUpdateDTO statusUpdateDTO);

    OrderDTO updateOrderStatus(Long id, OrderStatus status);

    OrderDTO updateTrackingInfo(Long id, String trackingNumber, String shippingCompany);

    OrderDTO refundOrder(Long id);

    void deleteOrder(Long id);

    List<OrderItemDTO> getOrderItems(Long orderId);

    void cancelAbandonedOrders();

    Map<String, Long> getOrderCountByStatus();

    List<OrderDTO> getRecentOrders(int limit);

    Map<String, Long> getOrderSummary();

    Map<String, Object> getOrderStatistics(String period, String startDate, String endDate);

    Map<String, Object> getSalesStatistics(String period, String startDate, String endDate);

    Page<RefundDTO> getRefunds(Pageable pageable);

    RefundDTO getRefundById(Long id);

    Page<CancellationDTO> getCancellations(Pageable pageable);

    CancellationDTO getCancellationById(Long id);

    Order createOrder(Order order);
    
    Optional<Order> findById(Long id);
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    Page<Order> findByMemberId(Long memberId, Pageable pageable);
    
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    
    Page<Order> findByMemberIdAndStatus(Long memberId, OrderStatus status, Pageable pageable);
    
    List<Order> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Order> findByMemberIdAndDateRange(Long memberId, LocalDateTime startDate, LocalDateTime endDate);
    
    long countByStatus(OrderStatus status);
    
    Map<OrderStatus, Long> getOrderStatusDistribution();
    
    List<Object[]> getDailySales(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Object[]> getMonthlySales(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Object[]> getTopCustomers(int limit);

    Page<OrderDTO> getCancelledOrders(Pageable pageable);
}