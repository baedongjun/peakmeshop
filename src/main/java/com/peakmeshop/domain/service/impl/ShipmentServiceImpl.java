package com.peakmeshop.domain.service.impl;

import com.peakmeshop.api.dto.OrderStatusUpdateDTO;
import com.peakmeshop.api.dto.ShipmentDTO;
import com.peakmeshop.api.dto.ShipmentTrackingDTO;
import com.peakmeshop.api.mapper.ShipmentMapper;
import com.peakmeshop.api.mapper.ShipmentTrackingMapper;
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
    private final ShipmentMapper shipmentMapper;
    private final ShipmentTrackingMapper shipmentTrackingMapper;

    @Override
    public Page<ShipmentDTO> getAllShipments(Pageable pageable) {
        return shipmentRepository.findAll(pageable)
                .map(shipmentMapper::toDTO);
    }

    @Override
    public Optional<ShipmentDTO> getShipmentById(Long id) {
        return shipmentRepository.findById(id)
                .map(shipmentMapper::toDTO);
    }

    @Override
    public List<ShipmentDTO> getShipmentsByOrderId(Long orderId) {
        return shipmentRepository.findByOrderId(orderId).stream()
                .map(shipmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ShipmentDTO> getShipmentByTrackingNumber(String trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber)
                .map(shipmentMapper::toDTO);
    }

    @Override
    public ShipmentDTO createShipment(ShipmentDTO shipmentDTO) {
        Order order = orderRepository.findById(shipmentDTO.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + shipmentDTO.getOrderId()));

        Shipment shipment = shipmentMapper.toEntity(shipmentDTO);
        shipment.setOrder(order);
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

        return shipmentMapper.toDTO(savedShipment);
    }

    @Override
    public ShipmentDTO updateShipment(Long id, ShipmentDTO shipmentDTO) {
        Shipment existingShipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("배송 정보를 찾을 수 없습니다: " + id));

        Shipment shipment = shipmentMapper.toEntity(shipmentDTO);
        shipment.setId(id);
        shipment.setOrder(existingShipment.getOrder());
        shipment.setUpdatedAt(LocalDateTime.now());

        if (shipmentDTO.getStatus() != null) {
            ShipmentStatus newStatus = shipmentDTO.getStatus();
            if (newStatus != existingShipment.getStatus()) {
                updateShipmentStatus(id, shipmentDTO.getStatus());
                return getShipmentById(id).orElseThrow(() -> new IllegalArgumentException("배송 정보를 찾을 수 없습니다: " + id));
            }
        }

        Shipment updatedShipment = shipmentRepository.save(shipment);

        log.info("배송 정보 업데이트: 배송 ID={}, 배송사={}, 운송장번호={}",
                id, shipment.getCarrier(), shipment.getTrackingNumber());

        return shipmentMapper.toDTO(updatedShipment);
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
    public ShipmentDTO updateShipmentStatus(Long id, ShipmentStatus statusStr) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("배송 정보를 찾을 수 없습니다: " + id));

        ShipmentStatus status;
        try {
            status = statusStr;
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

        return shipmentMapper.toDTO(updatedShipment);
    }

    @Override
    public ShipmentTrackingDTO addTrackingEvent(Long shipmentId, ShipmentTrackingDTO trackingDTO) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("배송 정보를 찾을 수 없습니다: " + shipmentId));

        ShipmentTracking tracking = shipmentTrackingMapper.toEntity(trackingDTO);
        tracking.setShipment(shipment);
        tracking.setStatusChangedAt(LocalDateTime.now());

        ShipmentTracking savedTracking = shipmentTrackingRepository.save(tracking);

        log.info("배송 추적 정보 추가: 배송 ID={}, 상태={}, 메모={}",
                shipmentId, tracking.getStatus(), tracking.getNote());

        return shipmentTrackingMapper.toDTO(savedTracking);
    }

    @Override
    public List<ShipmentTrackingDTO> getTrackingHistory(Long shipmentId) {
        return shipmentTrackingRepository.findByShipmentIdOrderByStatusChangedAtDesc(shipmentId)
                .stream()
                .map(shipmentTrackingMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShipmentTrackingDTO> getTrackingHistoryByTrackingNumber(String trackingNumber) {
        return shipmentTrackingRepository.findByShipmentTrackingNumberOrderByStatusChangedAtDesc(trackingNumber)
                .stream()
                .map(shipmentTrackingMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void syncTrackingInformation(Long shipmentId) {
        // 외부 배송 추적 API 연동 로직 구현
        // 예: 택배사 API를 통해 실시간 배송 상태 조회
        log.info("배송 추적 정보 동기화: 배송 ID={}", shipmentId);
    }

    @Override
    public void syncAllTrackingInformation() {
        // 모든 진행 중인 배송 건에 대해 추적 정보 동기화
        List<Shipment> activeShipments = shipmentRepository.findByStatusIn(
                List.of(ShipmentStatus.PREPARING, ShipmentStatus.PICKED_UP, ShipmentStatus.SHIPPED));

        for (Shipment shipment : activeShipments) {
            syncTrackingInformation(shipment.getId());
        }

        log.info("전체 배송 추적 정보 동기화 완료: 처리 건수={}", activeShipments.size());
    }
}