package com.peakmeshop.api.controller;

import com.peakmeshop.api.dto.OrderDTO;
import com.peakmeshop.api.dto.OrderItemDTO;
import com.peakmeshop.api.dto.OrderRequestDTO;
import com.peakmeshop.api.dto.OrderStatusUpdateDTO;
import com.peakmeshop.common.security.oauth2.user.UserPrincipal;
import com.peakmeshop.domain.entity.Order;
import com.peakmeshop.domain.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @orderService.getOrderById(#id).memberId == authentication.principal.id")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/number/{orderNumber}")
    @PreAuthorize("hasRole('ADMIN') or @orderService.getOrderByOrderNumber(#orderNumber).memberId == authentication.principal.id")
    public ResponseEntity<OrderDTO> getOrderByOrderNumber(@PathVariable String orderNumber) {
        OrderDTO order = orderService.getOrderByOrderNumber(orderNumber);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderDTO> createOrder(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody OrderRequestDTO orderRequestDTO) {

        OrderDTO createdOrder = orderService.createOrder(userPrincipal.getId(), orderRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @GetMapping("/my-orders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderDTO>> getMyOrders(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<OrderDTO> orders = orderService.getOrdersByMemberId(userPrincipal.getId());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/my-orders/paged")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<OrderDTO>> getMyOrdersPaged(
            @AuthenticationPrincipal UserPrincipal userPrincipal, Pageable pageable) {

        Page<OrderDTO> orders = orderService.getOrdersByMemberId(userPrincipal.getId(), pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}/items")
    @PreAuthorize("hasRole('ADMIN') or @orderService.getOrderById(#id).memberId == authentication.principal.id")
    public ResponseEntity<List<OrderItemDTO>> getOrderItems(@PathVariable Long id) {
        List<OrderItemDTO> orderItems = orderService.getOrderItems(id);
        return ResponseEntity.ok(orderItems);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id, @Valid @RequestBody OrderStatusUpdateDTO statusUpdateDTO) {

        OrderDTO updatedOrder = orderService.updateOrderStatus(id, statusUpdateDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/{id}/tracking")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> updateTrackingInfo(
            @PathVariable Long id,
            @RequestBody OrderStatusUpdateDTO trackingInfo) {

        OrderDTO updatedOrder = orderService.updateTrackingInfo(
                id, trackingInfo.getTrackingNumber(), trackingInfo.getShippingCompany());
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> refundOrder(@PathVariable Long id) {
        OrderDTO refundedOrder = orderService.refundOrder(id);
        return ResponseEntity.ok(refundedOrder);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}