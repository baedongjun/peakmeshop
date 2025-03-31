package com.peakmeshop.domain.service;

import java.util.List;
import java.util.Map;

import com.peakmeshop.api.dto.OrderStatusUpdateDTO;
import com.peakmeshop.domain.entity.Order;
import com.peakmeshop.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.OrderDTO;
import com.peakmeshop.api.dto.OrderItemDTO;
import com.peakmeshop.api.dto.OrderRequestDTO;

public interface OrderService {

    OrderDTO getOrderDTOById(Long orderId); // DTO를 반환하는 메서드
    Order getOrderEntityById(Long orderId);

    OrderDTO getOrderByOrderNumber(String orderNumber);

    List<OrderDTO> getOrdersByMemberId(Long memberId);

    Page<OrderDTO> getOrdersByMemberId(Long memberId, Pageable pageable);

    Page<OrderDTO> getAllOrders(Pageable pageable);

    Page<OrderDTO> getOrdersByStatus(OrderStatus status, Pageable pageable);

    OrderDTO createOrder(OrderDTO orderDTO);

    OrderDTO createOrder(Long memberId, OrderRequestDTO orderRequestDTO);

    OrderDTO updateOrderStatus(Long id, OrderStatusUpdateDTO statusUpdateDTO);

    OrderDTO updateTrackingInfo(Long id, String trackingNumber, String shippingCompany);

    OrderDTO refundOrder(Long id);

    void deleteOrder(Long id);

    List<OrderItemDTO> getOrderItems(Long orderId);

    void cancelAbandonedOrders();

    Map<String, Long> getOrderCountByStatus();

    List<OrderDTO> getRecentOrders(int limit);
}