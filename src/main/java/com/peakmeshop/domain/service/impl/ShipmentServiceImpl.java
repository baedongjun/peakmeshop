package com.peakmeshop.domain.service.impl;

import com.peakmeshop.api.dto.OrderStatusUpdateDTO;
import com.peakmeshop.api.dto.ShipmentDTO;
import com.peakmeshop.api.dto.ShipmentTrackingDTO;
import com.peakmeshop.domain.entity.Order;
import com.peakmeshop.domain.entity.Shipment;
import com.peakmeshop.domain.entity.ShipmentTracking;
import com.peakmeshop.domain.enums.OrderStatus;
import com.peakmeshop.domain.enums.ShipmentStatus;
import com.peakmeshop.domain.repository.OrderRepository;
import com.peakmeshop.domain.repository.ShipmentRepository;
import com.peakmeshop.domain.repository.ShipmentTrackingRepository;
import com.peakmeshop.domain.service.EmailService;
import com.peakmeshop.domain.service.OrderService;
import com.peakmeshop.domain.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentTrackingRepository shipmentTrackingRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final EmailService emailService;

    @Override
    public Page<ShipmentDTO> getAllShipments(Pageable pageable) {
        return shipmentRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public Optional<ShipmentDTO> getShipmentById(Long id) {
        return shipmentRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    public List<ShipmentDTO> getShipmentsByOrderId(Long orderId) {
        return shipmentRepository.findByOrderId(orderId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ShipmentDTO> getShipmentByTrackingNumber(String trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber)
                .map(this::convertToDTO);
    }

    @Override
    public ShipmentDTO createShipment(ShipmentDTO shipmentDTO) {
        Order order = orderRepository.findById(shipmentDTO.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + shipmentDTO.getOrderId()));

        Shipment shipment = new Shipment();
        shipment.setOrder(order);
        shipment.setCarrier(shipmentDTO.getCarrier());
        shipment.setTrackingNumber(shipmentDTO.getTrackingNumber());
        shipment.setShippingMethod(shipmentDTO.getShippingMethod());
        shipment.setStatus(ShipmentStatus.valueOf(shipmentDTO.getStatus()));
        shipment.setEstimatedDeliveryDate(shipmentDTO.getEstimatedDeliveryDate());
        shipment.setShippingAddress(shipmentDTO.getShippingAddress());
        shipment.setShippingCity(shipmentDTO.getShippingCity());
        shipment.setShippingState(shipmentDTO.getShippingState());
        shipment.setShippingZipCode(shipmentDTO.getShippingZipCode());
        shipment.setShippingCountry(shipmentDTO.getShippingCountry());
        shipment.setRecipientName(shipmentDTO.getRecipientName());
        shipment.setRecipientPhone(shipmentDTO.getRecipientPhone());
        shipment.setCreatedAt(LocalDateTime.now());

        Shipment savedShipment = shipmentRepository.save(shipment);

        // 주문 상태 업데이트
        OrderStatusUpdateDTO statusUpdateDTO = new OrderStatusUpdateDTO();
        statusUpdateDTO.setStatus(OrderStatus.PREPARING);
        if (shipment.getStatus() == ShipmentStatus.SHIPPED) {
            statusUpdateDTO.setStatus(OrderStatus.SHIPPED);
        } else if (shipment.getStatus() == ShipmentStatus.DELIVERED) {
            statusUpdateDTO.setStatus(OrderStatus.DELIVERED);
        }

        orderService.updateOrderStatus(order.getId(), statusUpdateDTO);

        // 배송 추적 정보 생성
        ShipmentTracking tracking = new ShipmentTracking();
        tracking.setShipment(savedShipment);
        tracking.setStatus(savedShipment.getStatus());
        tracking.setStatusChangedAt(LocalDateTime.now());
        tracking.setNote("배송 정보 생성");
        shipmentTrackingRepository.save(tracking);

        log.info("배송 정보 생성: 주문 ID={}, 배송사={}, 운송장번호={}",
                order.getId(), shipmentDTO.getCarrier(), shipmentDTO.getTrackingNumber());

        return convertToDTO(savedShipment);
    }

    @Override
    public ShipmentDTO updateShipment(Long id, ShipmentDTO shipmentDTO) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("배송 정보를 찾을 수 없습니다: " + id));

        if (shipmentDTO.getCarrier() != null) {
            shipment.setCarrier(shipmentDTO.getCarrier());
        }

        if (shipmentDTO.getTrackingNumber() != null) {
            shipment.setTrackingNumber(shipmentDTO.getTrackingNumber());
        }

        if (shipmentDTO.getShippingMethod() != null) {
            shipment.setShippingMethod(shipmentDTO.getShippingMethod());
        }

        if (shipmentDTO.getStatus() != null) {
            ShipmentStatus newStatus = ShipmentStatus.valueOf(shipmentDTO.getStatus());
            if (newStatus != shipment.getStatus()) {
                updateShipmentStatus(id, shipmentDTO.getStatus());
                return getShipmentById(id).orElseThrow(() -> new IllegalArgumentException("배송 정보를 찾을 수 없습니다: " + id));
            }
        }

        if (shipmentDTO.getEstimatedDeliveryDate() != null) {
            shipment.setEstimatedDeliveryDate(shipmentDTO.getEstimatedDeliveryDate());
        }

        if (shipmentDTO.getShippingAddress() != null) {
            shipment.setShippingAddress(shipmentDTO.getShippingAddress());
        }

        if (shipmentDTO.getShippingCity() != null) {
            shipment.setShippingCity(shipmentDTO.getShippingCity());
        }

        if (shipmentDTO.getShippingState() != null) {
            shipment.setShippingState(shipmentDTO.getShippingState());
        }

        if (shipmentDTO.getShippingZipCode() != null) {
            shipment.setShippingZipCode(shipmentDTO.getShippingZipCode());
        }

        if (shipmentDTO.getShippingCountry() != null) {
            shipment.setShippingCountry(shipmentDTO.getShippingCountry());
        }

        if (shipmentDTO.getRecipientName() != null) {
            shipment.setRecipientName(shipmentDTO.getRecipientName());
        }

        if (shipmentDTO.getRecipientPhone() != null) {
            shipment.setRecipientPhone(shipmentDTO.getRecipientPhone());
        }

        shipment.setUpdatedAt(LocalDateTime.now());

        Shipment updatedShipment = shipmentRepository.save(shipment);

        log.info("배송 정보 업데이트: 배송 ID={}, 배송사={}, 운송장번호={}",
                id, shipment.getCarrier(), shipment.getTrackingNumber());

        return convertToDTO(updatedShipment);
    }

    @Override
    public boolean deleteShipment(Long id) {
        if (!shipmentRepository.existsById(id)) {
            return false;
        }

        try {
            // 배송 추적 정보 삭제
            shipmentTrackingRepository.deleteByShipmentId(id);
            // 배송 정보 삭제
            shipmentRepository.deleteById(id);
            log.info("배송 정보 삭제: 배송 ID={}", id);
            return true;
        } catch (Exception e) {
            log.error("배송 정보 삭제 중 오류 발생: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public ShipmentDTO updateShipmentStatus(Long id, String statusStr) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("배송 정보를 찾을 수 없습니다: " + id));

        ShipmentStatus status;
        try {
            status = ShipmentStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 배송 상태입니다: " + statusStr);
        }

        ShipmentStatus oldStatus = shipment.getStatus();
        shipment.setStatus(status);
        shipment.setUpdatedAt(LocalDateTime.now());

        OrderStatusUpdateDTO statusUpdateDTO = new OrderStatusUpdateDTO();

        // 배송 상태에 따른 추가 처리
        switch (status) {
            case SHIPPED:
                shipment.setShippedAt(LocalDateTime.now());

                // 주문 상태 업데이트
                statusUpdateDTO.setStatus(OrderStatus.SHIPPED);
                orderService.updateOrderStatus(shipment.getOrder().getId(), statusUpdateDTO);

                // 배송 시작 이메일 발송
                try {
                    emailService.sendShipmentNotificationEmail(
                            shipment.getOrder().getMember().getEmail(),
                            shipment.getOrder().getId(),
                            shipment.getOrder().getOrderNumber(),
                            shipment.getCarrier(),
                            shipment.getTrackingNumber());
                } catch (Exception e) {
                    log.error("배송 알림 이메일 발송 실패: {}", e.getMessage());
                }
                break;

            case DELIVERED:
                shipment.setDeliveredAt(LocalDateTime.now());
                // 주문 상태 업데이트
                statusUpdateDTO.setStatus(OrderStatus.DELIVERED);
                orderService.updateOrderStatus(shipment.getOrder().getId(), statusUpdateDTO);

                // 배송 완료 이메일 발송
                try {
                    emailService.sendDeliveryCompletionEmail(
                            shipment.getOrder().getMember().getEmail(),
                            shipment.getOrder().getId(),
                            shipment.getOrder().getOrderNumber());
                } catch (Exception e) {
                    log.error("배송 완료 이메일 발송 실패: {}", e.getMessage());
                }
                break;

            default:
                break;
        }

        Shipment updatedShipment = shipmentRepository.save(shipment);

        // 배송 상태 변경 추적 기록
        ShipmentTracking tracking = new ShipmentTracking();
        tracking.setShipment(updatedShipment);
        tracking.setStatus(status);
        tracking.setStatusChangedAt(LocalDateTime.now());
        tracking.setNote("상태 변경: " + oldStatus + " -> " + status);
        shipmentTrackingRepository.save(tracking);

        log.info("배송 상태 업데이트: 배송 ID={}, 상태={}", id, status);

        return convertToDTO(updatedShipment);
    }

    @Override
    public ShipmentTrackingDTO addTrackingEvent(Long shipmentId, ShipmentTrackingDTO trackingDTO) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("배송 정보를 찾을 수 없습니다: " + shipmentId));

        ShipmentTracking tracking = new ShipmentTracking();
        tracking.setShipment(shipment);
        tracking.setStatus(ShipmentStatus.valueOf(trackingDTO.getStatus()));
        tracking.setStatusChangedAt(LocalDateTime.now());
        tracking.setNote(trackingDTO.getNote());
        tracking.setLocation(trackingDTO.getLocation());

        ShipmentTracking savedTracking = shipmentTrackingRepository.save(tracking);

        // 배송 상태 업데이트
        if (trackingDTO.getStatus() != null && !trackingDTO.getStatus().equals(shipment.getStatus().name())) {
            updateShipmentStatus(shipmentId, trackingDTO.getStatus());
        }

        log.info("배송 추적 정보 추가: 배송 ID={}, 상태={}, 위치={}",
                shipmentId, trackingDTO.getStatus(), trackingDTO.getLocation());

        return convertToTrackingDTO(savedTracking);
    }

    @Override
    public List<ShipmentTrackingDTO> getTrackingHistory(Long shipmentId) {
        List<ShipmentTracking> trackingHistory = shipmentTrackingRepository.findByShipmentIdOrderByStatusChangedAtDesc(shipmentId);
        return trackingHistory.stream()
                .map(this::convertToTrackingDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShipmentTrackingDTO> getTrackingHistoryByTrackingNumber(String trackingNumber) {
        Optional<Shipment> shipmentOpt = shipmentRepository.findByTrackingNumber(trackingNumber);
        if (shipmentOpt.isPresent()) {
            return getTrackingHistory(shipmentOpt.get().getId());
        }
        return List.of();
    }

    @Override
    public void syncTrackingInformation(Long shipmentId) {
        // 외부 배송 추적 API와 연동하여 배송 상태 업데이트
        // 이 메서드는 실제 구현에서 외부 API를 호출하여 배송 상태를 동기화해야 함
        log.info("배송 추적 정보 동기화: 배송 ID={}", shipmentId);

        // 예시: 외부 API 호출 후 상태 업데이트
        // 실제 구현에서는 이 부분을 외부 API 호출로 대체
        Optional<Shipment> shipmentOpt = shipmentRepository.findById(shipmentId);
        if (shipmentOpt.isPresent()) {
            Shipment shipment = shipmentOpt.get();
            // 예시로 배송 중인 경우 배송 완료로 업데이트
            if (shipment.getStatus() == ShipmentStatus.SHIPPED) {
                updateShipmentStatus(shipmentId, "DELIVERED");
            }
        }
    }

    @Override
    public void syncAllTrackingInformation() {
        // 모든 진행 중인 배송에 대해 추적 정보 동기화
        List<Shipment> activeShipments = shipmentRepository.findByStatusIn(
                List.of(ShipmentStatus.PREPARING, ShipmentStatus.SHIPPED));

        for (Shipment shipment : activeShipments) {
            try {
                syncTrackingInformation(shipment.getId());
            } catch (Exception e) {
                log.error("배송 추적 정보 동기화 중 오류 발생: 배송 ID={}, 오류={}", shipment.getId(), e.getMessage());
            }
        }

        log.info("모든 배송 추적 정보 동기화 완료: 처리된 배송 수={}", activeShipments.size());
    }

    // 헬퍼 메서드
    private ShipmentDTO convertToDTO(Shipment shipment) {
        ShipmentDTO dto = new ShipmentDTO();
        dto.setId(shipment.getId());
        dto.setOrderId(shipment.getOrder().getId());
        dto.setOrderNumber(shipment.getOrder().getOrderNumber());
        dto.setCarrier(shipment.getCarrier());
        dto.setTrackingNumber(shipment.getTrackingNumber());
        dto.setShippingMethod(shipment.getShippingMethod());
        dto.setStatus(shipment.getStatus().name());
        dto.setEstimatedDeliveryDate(shipment.getEstimatedDeliveryDate());
        dto.setShippingAddress(shipment.getShippingAddress());
        dto.setShippingCity(shipment.getShippingCity());
        dto.setShippingState(shipment.getShippingState());
        dto.setShippingZipCode(shipment.getShippingZipCode());
        dto.setShippingCountry(shipment.getShippingCountry());
        dto.setRecipientName(shipment.getRecipientName());
        dto.setRecipientPhone(shipment.getRecipientPhone());
        dto.setShippedAt(shipment.getShippedAt());
        dto.setDeliveredAt(shipment.getDeliveredAt());
        dto.setCreatedAt(shipment.getCreatedAt());
        dto.setUpdatedAt(shipment.getUpdatedAt());

        return dto;
    }

    private ShipmentTrackingDTO convertToTrackingDTO(ShipmentTracking tracking) {
        ShipmentTrackingDTO dto = new ShipmentTrackingDTO();
        dto.setId(tracking.getId());
        dto.setShipmentId(tracking.getShipment().getId());
        dto.setStatus(tracking.getStatus().name());
        dto.setStatusChangedAt(tracking.getStatusChangedAt());
        dto.setNote(tracking.getNote());
        dto.setLocation(tracking.getLocation());
        return dto;
    }
}