package com.peakmeshop.api.controller;

import java.util.List;
import java.util.Map;

import com.peakmeshop.api.dto.OrderDTO;
import com.peakmeshop.api.dto.OrderStatusUpdateDTO;
import com.peakmeshop.domain.enums.OrderStatus;
import com.peakmeshop.domain.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class OrderStatusController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getAllOrders(Pageable pageable) {
        Page<OrderDTO> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        OrderDTO order = orderService.getOrderDTOById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<OrderDTO>> getOrdersByStatus(
            @PathVariable OrderStatus status, Pageable pageable) {

        Page<OrderDTO> orders = orderService.getOrdersByStatus(status, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getOrderCountByStatus() {
        Map<String, Long> counts = orderService.getOrderCountByStatus();
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<OrderDTO>> getRecentOrders(
            @RequestParam(defaultValue = "10") int limit) {

        List<OrderDTO> orders = orderService.getRecentOrders(limit);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}/process")
    public ResponseEntity<OrderDTO> processOrder(@PathVariable Long id) {
        OrderStatusUpdateDTO statusUpdateDTO = OrderStatusUpdateDTO.builder()
                .status(OrderStatus.PROCESSING)
                .build();

        OrderDTO updatedOrder = orderService.updateOrderStatus(id, statusUpdateDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/{id}/ship")
    public ResponseEntity<OrderDTO> shipOrder(
            @PathVariable Long id,
            @RequestParam String trackingNumber,
            @RequestParam String shippingCompany) {

        OrderDTO updatedOrder = orderService.updateTrackingInfo(id, trackingNumber, shippingCompany);
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/{id}/deliver")
    public ResponseEntity<OrderDTO> deliverOrder(@PathVariable Long id) {
        OrderStatusUpdateDTO statusUpdateDTO = OrderStatusUpdateDTO.builder()
                .status(OrderStatus.DELIVERED)
                .build();

        OrderDTO updatedOrder = orderService.updateOrderStatus(id, statusUpdateDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderDTO> cancelOrder(
            @PathVariable Long id,
            @RequestParam String reason) {

        OrderStatusUpdateDTO statusUpdateDTO = OrderStatusUpdateDTO.builder()
                .status(OrderStatus.CANCELLED)
                .cancelReason(reason)
                .build();

        OrderDTO updatedOrder = orderService.updateOrderStatus(id, statusUpdateDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/{id}/refund")
    public ResponseEntity<OrderDTO> refundOrder(@PathVariable Long id) {
        OrderDTO refundedOrder = orderService.refundOrder(id);
        return ResponseEntity.ok(refundedOrder);
    }
}