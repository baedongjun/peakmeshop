package com.peakmeshop.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import com.peakmeshop.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.dto.OrderDTO;
import com.peakmeshop.dto.OrderItemDTO;
import com.peakmeshop.dto.OrderRequestDTO;
import com.peakmeshop.dto.OrderStatusUpdateDTO;
import com.peakmeshop.entity.Address;
import com.peakmeshop.entity.Member;
import com.peakmeshop.entity.Order;
import com.peakmeshop.entity.OrderItem;
import com.peakmeshop.entity.Product;
import com.peakmeshop.exception.ResourceNotFoundException;
import com.peakmeshop.repository.AddressRepository;
import com.peakmeshop.repository.MemberRepository;
import com.peakmeshop.repository.OrderItemRepository;
import com.peakmeshop.repository.OrderRepository;
import com.peakmeshop.repository.ProductRepository;
import com.peakmeshop.service.EmailService;
import com.peakmeshop.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final EmailService emailService;
    private final Random random = new Random();

    @Override
    public OrderDTO getOrderDTOById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));
        return convertToDTO(order);
    }

    @Override
    public Order getOrderEntityById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with order number: " + orderNumber));
        return convertToDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByMemberId(Long memberId) {
        List<Order> orders = orderRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByMemberId(Long memberId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);
        return orders.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        Page<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc(pageable);
        return orders.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        Page<Order> orders = orderRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        return orders.map(this::convertToDTO);
    }

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Member member = memberRepository.findById(orderDTO.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + orderDTO.getMemberId()));

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .member(member)
                .status(OrderStatus.PENDING)
                .subtotal(orderDTO.getSubtotal())
                .discountAmount(orderDTO.getDiscountAmount())
                .shippingCost(orderDTO.getShippingCost())
                .tax(orderDTO.getTax())
                .totalAmount(orderDTO.getTotalAmount())
                .paymentMethod(orderDTO.getPaymentMethod())
                .paymentStatus(orderDTO.getPaymentStatus())
                .shippingMethod(orderDTO.getShippingMethod())
                .createdAt(LocalDateTime.now())
                .build();

        // 배송지 및 청구지 주소 설정
        if (orderDTO.getShippingAddressId() != null) {
            Address shippingAddress = addressRepository.findById(orderDTO.getShippingAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shipping address not found with id: " + orderDTO.getShippingAddressId()));
            order.setShippingAddress(shippingAddress);
        }

        if (orderDTO.getBillingAddressId() != null) {
            Address billingAddress = addressRepository.findById(orderDTO.getBillingAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Billing address not found with id: " + orderDTO.getBillingAddressId()));
            order.setBillingAddress(billingAddress);
        }

        Order savedOrder = orderRepository.save(order);

        // 주문 항목 저장
        if (orderDTO.getItems() != null) {
            for (OrderItemDTO itemDTO : orderDTO.getItems()) {
                Product product = productRepository.findById(itemDTO.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDTO.getProductId()));

                OrderItem orderItem = OrderItem.builder()
                        .order(savedOrder)
                        .product(product)
                        .productName(product.getName())
                        .productImage(product.getMainImage())
                        .quantity(itemDTO.getQuantity())
                        .price(product.getPrice())
                        .discount(product.getDiscountAmount())
                        .options(itemDTO.getOptions())
                        .build();

                orderItemRepository.save(orderItem);

                // 재고 감소
                product.setStock(product.getStock() - itemDTO.getQuantity());
                productRepository.save(product);
            }
        }

        return getOrderDTOById(savedOrder.getId());
    }

    @Override
    @Transactional
    public OrderDTO createOrder(Long memberId, OrderRequestDTO orderRequestDTO) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));

        // 주문 생성
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .member(member)
                .status(OrderStatus.PENDING)
                .subtotal(orderRequestDTO.getSubtotal())
                .discountAmount(orderRequestDTO.getDiscountAmount())
                .shippingCost(orderRequestDTO.getShippingCost())
                .tax(orderRequestDTO.getTax())
                .totalAmount(orderRequestDTO.getTotalAmount())
                .paymentMethod(orderRequestDTO.getPaymentMethod())
                .paymentStatus("PENDING")
                .shippingMethod(orderRequestDTO.getShippingMethod())
                .createdAt(LocalDateTime.now())
                .build();

        // 배송지 주소 설정
        if (orderRequestDTO.getShippingAddressId() != null) {
            Address shippingAddress = addressRepository.findById(orderRequestDTO.getShippingAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shipping address not found with id: " + orderRequestDTO.getShippingAddressId()));
            order.setShippingAddress(shippingAddress);
        }

        // 청구지 주소 설정
        if (orderRequestDTO.getBillingAddressId() != null) {
            Address billingAddress = addressRepository.findById(orderRequestDTO.getBillingAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Billing address not found with id: " + orderRequestDTO.getBillingAddressId()));
            order.setBillingAddress(billingAddress);
        }

        Order savedOrder = orderRepository.save(order);

        // 주문 항목 저장
        if (orderRequestDTO.getItems() != null) {
            for (OrderItemDTO itemDTO : orderRequestDTO.getItems()) {
                Product product = productRepository.findById(itemDTO.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDTO.getProductId()));

                OrderItem orderItem = OrderItem.builder()
                        .order(savedOrder)
                        .product(product)
                        .productName(product.getName())
                        .productImage(product.getMainImage())
                        .quantity(itemDTO.getQuantity())
                        .price(product.getPrice())
                        .discount(product.getDiscountAmount())
                        .options(itemDTO.getOptions())
                        .build();

                orderItemRepository.save(orderItem);

                // 재고 감소
                product.setStock(product.getStock() - itemDTO.getQuantity());
                productRepository.save(product);
            }
        }

        return getOrderDTOById(savedOrder.getId());
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long id, OrderStatusUpdateDTO statusUpdateDTO) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        OrderStatus oldStatus = order.getStatus();
        OrderStatus newStatus = statusUpdateDTO.getStatus();

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());

        // 상태별 추가 처리
        switch (newStatus) {
            case COMPLETED:
                order.setPaidAt(LocalDateTime.now());
                order.setPaymentStatus("COMPLETED");
                order.setPaymentTransactionId(statusUpdateDTO.getTransactionId());
                break;
            case SHIPPED:
                order.setShippedAt(LocalDateTime.now());
                order.setTrackingNumber(statusUpdateDTO.getTrackingNumber());
                order.setShippingCompany(statusUpdateDTO.getShippingCompany());

                // 배송 알림 이메일 발송
                if (statusUpdateDTO.getTrackingNumber() != null) {
                    // 257줄 부근 - 주문 확인 이메일 전송
                    emailService.sendOrderConfirmationEmail(
                            order.getMember().getEmail(),
                            order.getId(),
                            String.valueOf(order.getOrderNumber())  // Long을 String으로 변환
                    );
                }
                break;
            case DELIVERED:
                order.setDeliveredAt(LocalDateTime.now());
                break;
            case CANCELLED:
                order.setCancelledAt(LocalDateTime.now());
                order.setCancelReason(statusUpdateDTO.getReason());

                // 취소된 상품 재고 복구
                restoreProductStock(order);
                break;
            case REFUNDED:
                order.setRefundedAt(LocalDateTime.now());
                order.setRefundReason(statusUpdateDTO.getReason());

                // 환불된 상품 재고 복구 (이미 취소되지 않은 경우)
                if (!OrderStatus.CANCELLED.equals(oldStatus)) {
                    restoreProductStock(order);
                }
                break;
        }

        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Override
    @Transactional
    public OrderDTO updateTrackingInfo(Long id, String trackingNumber, String shippingCompany) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        order.setTrackingNumber(trackingNumber);
        order.setShippingCompany(shippingCompany);
        order.setUpdatedAt(LocalDateTime.now());

        // 배송 상태로 변경
        if (OrderStatus.PROCESSING.equals(order.getStatus())) {
            order.setStatus(OrderStatus.SHIPPED);
            order.setShippedAt(LocalDateTime.now());

            // 배송 알림 이메일 발송
            emailService.sendRefundConfirmationEmail(
                    order.getMember().getEmail(),
                    order.getId(),
                    String.valueOf(order.getOrderNumber())  // Long을 String으로 변환
            );
        }

        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Override
    @Transactional
    public OrderDTO refundOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // 환불 가능한 상태인지 확인
        if (!OrderStatus.DELIVERED.equals(order.getStatus()) &&
                !OrderStatus.SHIPPED.equals(order.getStatus()) &&
                !OrderStatus.PROCESSING.equals(order.getStatus()) &&
                !OrderStatus.COMPLETED.equals(order.getStatus())) {
            throw new IllegalStateException("Order cannot be refunded in current status: " + order.getStatus());
        }

        OrderStatusUpdateDTO statusUpdateDTO = OrderStatusUpdateDTO.builder()
                .status(OrderStatus.REFUNDED)
                .reason("고객 요청에 의한 환불")
                .build();

        return updateOrderStatus(id, statusUpdateDTO);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // 주문 항목 삭제
        orderItemRepository.deleteByOrderId(id);

        // 주문 삭제
        orderRepository.delete(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemDTO> getOrderItems(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        return orderItems.stream()
                .map(this::convertToItemDTO)
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
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private String generateOrderNumber() {
        // 주문번호 생성 로직 (예: 날짜 + 랜덤 숫자)
        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = String.format("%06d", random.nextInt(1000000));
        return "ORD-" + datePrefix + "-" + randomSuffix;
    }

    private void restoreProductStock(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }
    }

    private OrderDTO convertToDTO(Order order) {
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(this::convertToItemDTO)
                .collect(Collectors.toList());

        return OrderDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .memberId(order.getMember().getId())
                .memberName(order.getMember().getName())
                .memberEmail(order.getMember().getEmail())
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount())
                .shippingCost(order.getShippingCost())
                .tax(order.getTax())
                .totalAmount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .paymentTransactionId(order.getPaymentTransactionId())
                .shippingMethod(order.getShippingMethod())
                .trackingNumber(order.getTrackingNumber())
                .shippingCompany(order.getShippingCompany())
                .cancelReason(order.getCancelReason())
                .refundReason(order.getRefundReason())
                .paidAt(order.getPaidAt())
                .shippedAt(order.getShippedAt())
                .deliveredAt(order.getDeliveredAt())
                .cancelledAt(order.getCancelledAt())
                .refundedAt(order.getRefundedAt())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(itemDTOs)
                .build();
    }

    private OrderItemDTO convertToItemDTO(OrderItem orderItem) {
        return OrderItemDTO.builder()
                .id(orderItem.getId())
                .orderId(orderItem.getOrder().getId())
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProductName())
                .productImage(orderItem.getProductImage())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .discount(orderItem.getDiscount())
                .totalPrice(calculateTotalPrice(orderItem))
                .options(orderItem.getOptions())
                .build();
    }

    private BigDecimal calculateTotalPrice(OrderItem orderItem) {
        BigDecimal itemPrice = orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()));
        if (orderItem.getDiscount() != null) {
            itemPrice = itemPrice.subtract(orderItem.getDiscount().multiply(new BigDecimal(orderItem.getQuantity())));
        }
        return itemPrice;
    }
}