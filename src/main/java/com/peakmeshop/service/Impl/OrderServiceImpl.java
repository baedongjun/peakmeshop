package com.peakmeshop.service.Impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.dto.OrderDTO;
import com.peakmeshop.dto.OrderItemDTO;
import com.peakmeshop.entity.Member;
import com.peakmeshop.entity.Order;
import com.peakmeshop.entity.OrderItem;
import com.peakmeshop.entity.Product;
import com.peakmeshop.exception.InsufficientStockException;
import com.peakmeshop.exception.ResourceNotFoundException;
import com.peakmeshop.repository.MemberRepository;
import com.peakmeshop.repository.OrderRepository;
import com.peakmeshop.repository.ProductRepository;
import com.peakmeshop.service.EmailService;
import com.peakmeshop.service.OrderService;
import com.peakmeshop.service.PaymentService;

import jakarta.mail.MessagingException;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final PaymentService paymentService;
    private final EmailService emailService;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            MemberRepository memberRepository,
            ProductRepository productRepository,
            PaymentService paymentService,
            EmailService emailService) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
        this.paymentService = paymentService;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public OrderDTO createOrder(Long memberId, List<OrderItemDTO> items, String shippingAddress,
                                String shippingDetailAddress, String shippingZipCode, String recipientName,
                                String recipientPhone, String paymentMethod, String impUid) {

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다: " + memberId));

        // 주문 생성
        Order order = new Order();
        order.setMember(member);
        order.setOrderNumber(generateOrderNumber());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setShippingAddress(shippingAddress);
        order.setShippingDetailAddress(shippingDetailAddress);
        order.setShippingZipCode(shippingZipCode);
        order.setRecipientName(recipientName);
        order.setRecipientPhone(recipientPhone);
        order.setPaymentMethod(paymentMethod);

        // 주문 상품 추가 및 총액 계산
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemDTO itemDTO : items) {
            Product product = productRepository.findById(itemDTO.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다: " + itemDTO.productId()));

            // 재고 확인
            if (product.getStockQuantity() < itemDTO.quantity()) {
                throw new InsufficientStockException("상품 재고가 부족합니다: " + product.getName());
            }

            // 재고 감소
            product.setStockQuantity(product.getStockQuantity() - itemDTO.quantity());
            productRepository.save(product);

            // 주문 상품 생성
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(itemDTO.quantity());
            orderItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.quantity())));

            order.getOrderItems().add(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(totalAmount);

        // 결제 정보 검증 (아임포트 등의 결제 모듈 사용 시)
        if (impUid != null && !impUid.isEmpty()) {
            var paymentInfo = paymentService.getPaymentInfo(impUid);

            // 결제 금액 검증
            if (!paymentInfo.amount().equals(totalAmount)) {
                throw new IllegalArgumentException("결제 금액이 일치하지 않습니다.");
            }

            // 결제 완료 처리
            order.setStatus(Order.OrderStatus.PAID);
            order.setPaymentDate(LocalDateTime.now());
        }

        // 주문 저장
        Order savedOrder = orderRepository.save(order);

        // 주문 확인 이메일 발송
        try {
            emailService.sendOrderConfirmation(
                    member.getEmail(),
                    member.getName(),
                    savedOrder.getOrderNumber());
        } catch (MessagingException e) {
            // 이메일 발송 실패 로깅
        }

        return convertToDTO(savedOrder);
    }

    // 주문번호 생성
    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Entity -> DTO 변환
    private OrderDTO convertToDTO(Order order) {
        List<OrderItemDTO> itemDTOs = order.getOrderItems().stream()
                .map(item -> new OrderItemDTO(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProductName(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getSubtotal()))
                .collect(Collectors.toList());

        return new OrderDTO(
                order.getId(),
                order.getOrderNumber(),
                order.getMember().getId(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getPaymentMethod(),
                order.getShippingAddress(),
                order.getShippingDetailAddress(),
                order.getShippingZipCode(),
                order.getRecipientName(),
                order.getRecipientPhone(),
                order.getOrderDate(),
                order.getPaymentDate(),
                order.getShippingDate(),
                order.getCompletionDate(),
                itemDTOs);
    }
}