package com.peakmeshop.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peakmeshop.dto.MemberDTO;
import com.peakmeshop.dto.OrderCreateRequestDTO;
import com.peakmeshop.dto.OrderDTO;
import com.peakmeshop.dto.OrderStatusUpdateDTO;
import com.peakmeshop.dto.OrderSummaryDTO;
import com.peakmeshop.service.MemberService;
import com.peakmeshop.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;

    public OrderController(OrderService orderService, MemberService memberService) {
        this.orderService = orderService;
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(
            @Valid @RequestBody OrderCreateRequestDTO request,
            Principal principal) {

        // 현재 로그인한 회원 정보 조회
        MemberDTO member = memberService.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("로그인 정보를 찾을 수 없습니다."));

        // 주문 생성
        OrderDTO order = orderService.createOrder(
                member.id(),
                request.items().stream()
                        .map(item -> new OrderItemDTO(null, item.productId(), null, null, item.quantity(), null))
                        .toList(),
                request.shippingAddress(),
                request.shippingDetailAddress(),
                request.shippingZipCode(),
                request.recipientName(),
                request.recipientPhone(),
                request.paymentMethod(),
                request.impUid());

        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping
    public ResponseEntity<Page<OrderSummaryDTO>> getMyOrders(
            Principal principal,
            @PageableDefault(size = 10) Pageable pageable) {

        // 현재 로그인한 회원 정보 조회
        MemberDTO member = memberService.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("로그인 정보를 찾을 수 없습니다."));

        // 회원의 주문 목록 조회
        Page<OrderSummaryDTO> orders = orderService.getOrdersByMemberId(member.id(), pageable);

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @orderSecurity.isOrderOwner(#id, principal)")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateDTO request) {

        OrderDTO updatedOrder = orderService.updateOrderStatus(id, request.status(), request.reason());
        return ResponseEntity.ok(updatedOrder);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderSummaryDTO>> getAllOrders(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<OrderSummaryDTO> orders = orderService.getAllOrders(status, pageable);
        return ResponseEntity.ok(orders);
    }
}