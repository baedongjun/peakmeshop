package com.peakmeshop.domain.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.peakmeshop.domain.enums.OrderStatus;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.OrderStatusUpdateDTO;
import com.peakmeshop.api.dto.ShippingDTO;
import com.peakmeshop.api.mapper.ShippingMapper;
import com.peakmeshop.domain.entity.Order;
import com.peakmeshop.domain.entity.Shipping;
import com.peakmeshop.domain.repository.OrderRepository;
import com.peakmeshop.domain.repository.ShippingRepository;
import com.peakmeshop.domain.service.EmailService;
import com.peakmeshop.domain.service.OrderService;
import com.peakmeshop.domain.service.ShippingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShippingServiceImpl implements ShippingService {

    private final ShippingRepository shippingRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final EmailService emailService;
    private final ShippingMapper shippingMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ShippingDTO> getAllShippings(Pageable pageable) {
        return shippingRepository.findAll(pageable)
                .map(shippingMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ShippingDTO> getShippingById(Long id) {
        return shippingRepository.findById(id)
                .map(shippingMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ShippingDTO> getShippingByOrderId(Long orderId) {
        return shippingRepository.findByOrderId(orderId)
                .map(shippingMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ShippingDTO> getShippingByTrackingNumber(String trackingNumber) {
        return shippingRepository.findByTrackingNumber(trackingNumber)
                .map(shippingMapper::toDTO);
    }

    @Override
    @Transactional
    public ShippingDTO createShipping(ShippingDTO shippingDTO) {
        // 주문 조회
        Order order = orderRepository.findById(shippingDTO.orderId())
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다. ID: " + shippingDTO.orderId()));

        // 이미 배송 정보가 있는지 확인
        if (shippingRepository.existsByOrderId(shippingDTO.orderId())) {
            throw new IllegalStateException("이미 배송 정보가 등록된 주문입니다.");
        }

        // 배송 정보 생성
        Shipping shipping = shippingMapper.toEntity(shippingDTO);
        shipping.setOrder(order);
        shipping.setTrackingNumber(generateTrackingNumber());
        shipping.setCreatedAt(LocalDateTime.now());
        shipping.setUpdatedAt(LocalDateTime.now());

        Shipping savedShipping = shippingRepository.save(shipping);

        // 주문 상태 업데이트
        if (OrderStatus.SHIPPED.equals(shippingDTO.status())) {
            // OrderStatusUpdateDTO 객체 생성
            OrderStatusUpdateDTO statusUpdateDTO = OrderStatusUpdateDTO.builder()
                    .status(OrderStatus.SHIPPED)
                    .trackingNumber(shipping.getTrackingNumber())
                    .shippingCompany(shipping.getShippingCompany())
                    .build();

            orderService.updateOrderStatus(order.getId(), statusUpdateDTO);
        }

        // 배송 시작 이메일 발송
        if (order.getMember() != null && order.getMember().getEmail() != null) {
            // 배송 시작 이메일 발송 로직 구현
        }

        return shippingMapper.toDTO(savedShipping);
    }

    @Override
    @Transactional
    public ShippingDTO updateShippingStatus(Long id, String status) {
        Shipping shipping = shippingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("배송 정보를 찾을 수 없습니다. ID: " + id));

        shipping.setStatus(status);
        shipping.setUpdatedAt(LocalDateTime.now());

        // 배송 완료 처리
        if (OrderStatus.DELIVERED.equals(status)) {
            shipping.setDeliveredAt(LocalDateTime.now());

            // OrderStatusUpdateDTO 객체 생성
            OrderStatusUpdateDTO statusUpdateDTO = OrderStatusUpdateDTO.builder()
                    .status(OrderStatus.COMPLETED)
                    .trackingNumber(shipping.getTrackingNumber())
                    .shippingCompany(shipping.getShippingCompany())
                    .build();

            orderService.updateOrderStatus(shipping.getOrder().getId(), statusUpdateDTO);
        }

        Shipping updatedShipping = shippingRepository.save(shipping);

        // 배송 상태 변경 이메일 발송
        if (shipping.getOrder().getMember() != null && shipping.getOrder().getMember().getEmail() != null) {
            // 배송 상태 변경 이메일 발송 로직 구현
        }

        return shippingMapper.toDTO(updatedShipping);
    }

    @Override
    @Transactional
    public ShippingDTO updateTrackingInfo(Long id, String carrier, String trackingNumber) {
        Shipping shipping = shippingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("배송 정보를 찾을 수 없습니다. ID: " + id));

        shipping.setShippingCompany(carrier);
        shipping.setTrackingNumber(trackingNumber);
        shipping.setUpdatedAt(LocalDateTime.now());

        Shipping updatedShipping = shippingRepository.save(shipping);

        // 배송 추적 정보 변경 이메일 발송
        if (shipping.getOrder().getMember() != null && shipping.getOrder().getMember().getEmail() != null) {
            // 배송 추적 정보 변경 이메일 발송 로직 구현
        }

        return shippingMapper.toDTO(updatedShipping);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShippingDTO> getShippingsByStatus(String status, Pageable pageable) {
        return shippingRepository.findByStatus(status, pageable)
                .stream()
                .map(shippingMapper::toDTO)
                .collect(Collectors.toList());
    }

    // 운송장 번호 생성
    private String generateTrackingNumber() {
        return "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}