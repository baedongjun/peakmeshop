package com.peakmeshop.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.dto.OrderStatusUpdateDTO;
import com.peakmeshop.dto.RefundDTO;
import com.peakmeshop.dto.RefundItemDTO;
import com.peakmeshop.dto.RefundRequestDTO;
import com.peakmeshop.entity.Order;
import com.peakmeshop.entity.Refund;
import com.peakmeshop.entity.RefundItem;
import com.peakmeshop.enums.OrderStatus;
import com.peakmeshop.enums.RefundStatus;
import com.peakmeshop.repository.OrderRepository;
import com.peakmeshop.repository.RefundItemRepository;
import com.peakmeshop.repository.RefundRepository;
import com.peakmeshop.service.EmailService;
import com.peakmeshop.service.OrderService;
import com.peakmeshop.service.PaymentService;
import com.peakmeshop.service.RefundService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RefundServiceImpl implements RefundService {

    private final RefundRepository refundRepository;
    private final RefundItemRepository refundItemRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final EmailService emailService;

    @Override
    public RefundDTO requestRefund(RefundRequestDTO refundRequest) {
        Order order = orderService.getOrderEntityById(refundRequest.getOrderId());

        // 주문 상태 확인
        if (order.getStatus().equals(OrderStatus.CANCELLED) ||
                order.getStatus().equals(OrderStatus.REFUNDED) ||
                order.getStatus().equals(OrderStatus.PARTIALLY_REFUNDED)) {
            throw new IllegalStateException("이미 취소되거나 환불된 주문입니다. 현재 상태: " + order.getStatus());
        }

        // 환불 금액 검증
        if (refundRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0 ||
                refundRequest.getAmount().compareTo(order.getTotalAmount()) > 0) {
            throw new IllegalArgumentException("유효하지 않은 환불 금액입니다: " + refundRequest.getAmount());
        }

        // 이미 환불 요청이 있는지 확인
        List<Refund> existingRefunds = refundRepository.findByOrderId(order.getId());
        BigDecimal totalRefundedAmount = existingRefunds.stream()
                .filter(r -> r.getStatus() != RefundStatus.REJECTED)
                .map(Refund::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalRefundedAmount.add(refundRequest.getAmount()).compareTo(order.getTotalAmount()) > 0) {
            throw new IllegalArgumentException("총 환불 금액이 주문 금액을 초과합니다.");
        }

        Refund refund = new Refund();
        refund.setOrder(order);
        refund.setMember(order.getMember());
        refund.setAmount(refundRequest.getAmount());
        refund.setReason(refundRequest.getReason());
        refund.setStatus(RefundStatus.PENDING);
        refund.setRequestedAt(LocalDateTime.now());

        Refund savedRefund = refundRepository.save(refund);

        // 환불 요청 이메일 발송
        try {
            emailService.sendRefundRequestEmail(
                    order.getMember().getEmail(),
                    order.getId(),
                    order.getOrderNumber());
        } catch (Exception e) {
            log.error("환불 요청 이메일 발송 실패: {}", e.getMessage());
        }

        log.info("환불 요청 생성: 주문 ID={}, 금액={}, 사유={}", order.getId(), refundRequest.getAmount(), refundRequest.getReason());

        return convertToDTO(savedRefund);
    }

    @Override
    public RefundDTO approveRefund(Long refundId, String adminNote) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("환불 요청을 찾을 수 없습니다: " + refundId));

        if (refund.getStatus() != RefundStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 환불 요청입니다. 현재 상태: " + refund.getStatus());
        }

        // 결제 서비스를 통한 환불 처리
        try {
            // Order 클래스에 paymentId 필드가 없는 경우 주문 번호를 사용
            String paymentReference = refund.getOrder().getOrderNumber(); // getPaymentId() 대신 getOrderNumber() 사용

            boolean refundProcessed = paymentService.processRefund(paymentReference, refund.getAmount());

            if (!refundProcessed) {
                throw new RuntimeException("환불 처리 중 오류가 발생했습니다.");
            }
        } catch (Exception e) {
            log.error("환불 처리 중 오류: {}", e.getMessage());
            throw new RuntimeException("환불 처리 중 오류가 발생했습니다: " + e.getMessage());
        }

        refund.setStatus(RefundStatus.APPROVED);
        refund.setAdminNote(adminNote);
        refund.setProcessedAt(LocalDateTime.now());

        Refund updatedRefund = refundRepository.save(refund);

        // 주문 상태 업데이트
        Order order = refund.getOrder();
        if (refund.getAmount().compareTo(order.getTotalAmount()) == 0) {
            // 전액 환불인 경우
            OrderStatusUpdateDTO statusUpdateDTO = new OrderStatusUpdateDTO();
            statusUpdateDTO.setStatus(OrderStatus.REFUNDED);
            orderService.updateOrderStatus(order.getId(), statusUpdateDTO);
        } else {
            // 부분 환불인 경우
            OrderStatusUpdateDTO statusUpdateDTO = new OrderStatusUpdateDTO();
            statusUpdateDTO.setStatus(OrderStatus.PARTIALLY_REFUNDED);
            orderService.updateOrderStatus(order.getId(), statusUpdateDTO);
        }

        // 환불 확인 이메일 발송
        try {
            emailService.sendRefundConfirmationEmail(
                    refund.getMember().getEmail(),
                    order.getId(),
                    order.getOrderNumber());
        } catch (Exception e) {
            log.error("환불 확인 이메일 발송 실패: {}", e.getMessage());
        }

        log.info("환불 승인: 환불 ID={}, 주문 ID={}, 금액={}", refundId, order.getId(), refund.getAmount());

        return convertToDTO(updatedRefund);
    }

    @Override
    public RefundDTO rejectRefund(Long refundId, String adminNote) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("환불 요청을 찾을 수 없습니다: " + refundId));

        if (refund.getStatus() != RefundStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 환불 요청입니다. 현재 상태: " + refund.getStatus());
        }

        refund.setStatus(RefundStatus.REJECTED);
        refund.setAdminNote(adminNote);
        refund.setProcessedAt(LocalDateTime.now());

        Refund updatedRefund = refundRepository.save(refund);

        // 환불 거절 이메일 발송
        try {
            emailService.sendRefundRejectionEmail(
                    refund.getMember().getEmail(),
                    refund.getOrder().getId(),
                    refund.getOrder().getOrderNumber(),
                    adminNote);
        } catch (Exception e) {
            log.error("환불 거절 이메일 발송 실패: {}", e.getMessage());
        }

        log.info("환불 거절: 환불 ID={}, 주문 ID={}, 사유={}", refundId, refund.getOrder().getId(), adminNote);

        return convertToDTO(updatedRefund);
    }

    @Override
    public List<RefundDTO> getAllRefunds() {
        return refundRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RefundDTO> getRefundsByOrderId(Long orderId) {
        return refundRepository.findByOrderId(orderId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RefundDTO> getRefundsByMemberId(Long memberId) {
        return refundRepository.findByMemberId(memberId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RefundDTO getRefundById(Long refundId) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("환불 요청을 찾을 수 없습니다: " + refundId));
        return convertToDTO(refund);
    }

    @Override
    public List<RefundDTO> getRefundsByStatus(RefundStatus status) {
        return refundRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<RefundDTO> getRefundsByMemberId(Long memberId, Pageable pageable) {
        Page<Refund> refunds = refundRepository.findByMemberId(memberId, pageable);
        return refunds.map(this::convertToDTO);
    }

    @Override
    public RefundDTO getRefundByRefundNumber(String refundNumber) {
        Refund refund = refundRepository.findByRefundNumber(refundNumber)
                .orElseThrow(() -> new IllegalArgumentException("환불 요청을 찾을 수 없습니다: " + refundNumber));
        return convertToDTO(refund);
    }

    @Override
    public RefundDTO createRefund(RefundDTO refundDTO) {
        Order order = orderService.getOrderEntityById(refundDTO.getOrderId());

        Refund refund = new Refund();
        refund.setOrder(order);
        refund.setMember(order.getMember());
        refund.setRefundNumber(generateRefundNumber());
        refund.setAmount(refundDTO.getAmount());
        refund.setReason(refundDTO.getReason());
        refund.setStatus(RefundStatus.PENDING);
        refund.setRequestedAt(LocalDateTime.now());

        if (refundDTO.getShippingAmount() != null) {
            refund.setShippingAmount(refundDTO.getShippingAmount());
        }

        Refund savedRefund = refundRepository.save(refund);

        // 환불 아이템 처리
        if (refundDTO.getItems() != null && !refundDTO.getItems().isEmpty()) {
            saveRefundItems(savedRefund, refundDTO.getItems());
        }

        // 환불 요청 이메일 발송
        try {
            emailService.sendRefundRequestEmail(
                    order.getMember().getEmail(),
                    order.getId(),
                    order.getOrderNumber());
        } catch (Exception e) {
            log.error("환불 요청 이메일 발송 실패: {}", e.getMessage());
        }

        log.info("환불 요청 생성: 주문 ID={}, 금액={}, 사유={}", order.getId(), refundDTO.getAmount(), refundDTO.getReason());

        return convertToDTO(savedRefund);
    }

    @Override
    public RefundDTO updateRefund(Long refundId, RefundDTO refundDTO) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("환불 요청을 찾을 수 없습니다: " + refundId));

        if (refund.getStatus() != RefundStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 환불 요청은 수정할 수 없습니다. 현재 상태: " + refund.getStatus());
        }

        if (refundDTO.getAmount() != null) {
            refund.setAmount(refundDTO.getAmount());
        }

        if (refundDTO.getReason() != null) {
            refund.setReason(refundDTO.getReason());
        }

        if (refundDTO.getShippingAmount() != null) {
            refund.setShippingAmount(refundDTO.getShippingAmount());
        }

        if (refundDTO.getAdminNotes() != null) {
            refund.setAdminNote(refundDTO.getAdminNotes());
        }

        Refund updatedRefund = refundRepository.save(refund);

        // 환불 아이템 업데이트
        if (refundDTO.getItems() != null && !refundDTO.getItems().isEmpty()) {
            // 기존 아이템 삭제
            refundItemRepository.deleteByRefundId(refundId);
            // 새 아이템 저장
            saveRefundItems(updatedRefund, refundDTO.getItems());
        }

        log.info("환불 요청 업데이트: 환불 ID={}, 금액={}, 사유={}", refundId, refund.getAmount(), refund.getReason());

        return convertToDTO(updatedRefund);
    }

    @Override
    public RefundDTO updateRefundStatus(Long refundId, String status, String adminNote) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("환불 요청을 찾을 수 없습니다: " + refundId));

        RefundStatus newStatus;
        try {
            newStatus = RefundStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 환불 상태입니다: " + status);
        }

        refund.setStatus(newStatus);
        refund.setAdminNote(adminNote);

        switch (newStatus) {
            case APPROVED:
                refund.setProcessedAt(LocalDateTime.now());
                break;
            case REJECTED:
                refund.setRejectedAt(LocalDateTime.now());
                break;
            case COMPLETED:
                refund.setCompletedAt(LocalDateTime.now());
                break;
            default:
                break;
        }

        Refund updatedRefund = refundRepository.save(refund);

        // 주문 상태 업데이트
        if (newStatus == RefundStatus.APPROVED || newStatus == RefundStatus.COMPLETED) {
            Order order = refund.getOrder();
            if (refund.getAmount().compareTo(order.getTotalAmount()) == 0) {
                // 전액 환불인 경우
                OrderStatusUpdateDTO statusUpdateDTO = new OrderStatusUpdateDTO();
                statusUpdateDTO.setStatus(OrderStatus.REFUNDED);
                orderService.updateOrderStatus(order.getId(), statusUpdateDTO);
            } else {
                // 부분 환불인 경우
                OrderStatusUpdateDTO statusUpdateDTO = new OrderStatusUpdateDTO();
                statusUpdateDTO.setStatus(OrderStatus.PARTIALLY_REFUNDED);
                orderService.updateOrderStatus(order.getId(), statusUpdateDTO);
            }
        }

        // 이메일 발송
        try {
            if (newStatus == RefundStatus.APPROVED) {
                emailService.sendRefundConfirmationEmail(
                        refund.getMember().getEmail(),
                        refund.getOrder().getId(),
                        refund.getOrder().getOrderNumber());
            } else if (newStatus == RefundStatus.REJECTED) {
                emailService.sendRefundRejectionEmail(
                        refund.getMember().getEmail(),
                        refund.getOrder().getId(),
                        refund.getOrder().getOrderNumber(),
                        adminNote);
            }
        } catch (Exception e) {
            log.error("환불 상태 변경 이메일 발송 실패: {}", e.getMessage());
        }

        log.info("환불 상태 업데이트: 환불 ID={}, 상태={}, 관리자 메모={}", refundId, newStatus, adminNote);

        return convertToDTO(updatedRefund);
    }

    @Override
    public RefundDTO processRefund(Long refundId, String transactionId, String adminNote) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("환불 요청을 찾을 수 없습니다: " + refundId));

        if (refund.getStatus() != RefundStatus.PENDING && refund.getStatus() != RefundStatus.APPROVED) {
            throw new IllegalStateException("처리할 수 없는 환불 요청 상태입니다. 현재 상태: " + refund.getStatus());
        }

        // 결제 서비스를 통한 환불 처리
        try {
            String paymentReference = refund.getOrder().getOrderNumber();

            boolean refundProcessed = paymentService.processRefund(paymentReference, refund.getAmount());

            if (!refundProcessed) {
                throw new RuntimeException("환불 처리 중 오류가 발생했습니다.");
            }

            refund.setTransactionId(transactionId);
            refund.setStatus(RefundStatus.APPROVED);
            refund.setAdminNote(adminNote);
            refund.setProcessedAt(LocalDateTime.now());

            Refund updatedRefund = refundRepository.save(refund);

            // 주문 상태 업데이트
            Order order = refund.getOrder();
            if (refund.getAmount().compareTo(order.getTotalAmount()) == 0) {
                // 전액 환불인 경우
                OrderStatusUpdateDTO statusUpdateDTO = new OrderStatusUpdateDTO();
                statusUpdateDTO.setStatus(OrderStatus.REFUNDED);
                orderService.updateOrderStatus(order.getId(), statusUpdateDTO);
            } else {
                // 부분 환불인 경우
                OrderStatusUpdateDTO statusUpdateDTO = new OrderStatusUpdateDTO();
                statusUpdateDTO.setStatus(OrderStatus.PARTIALLY_REFUNDED);
                orderService.updateOrderStatus(order.getId(), statusUpdateDTO);
            }

            // 환불 확인 이메일 발송
            try {
                emailService.sendRefundConfirmationEmail(
                        refund.getMember().getEmail(),
                        order.getId(),
                        order.getOrderNumber());
            } catch (Exception e) {
                log.error("환불 확인 이메일 발송 실패: {}", e.getMessage());
            }

            log.info("환불 처리 완료: 환불 ID={}, 주문 ID={}, 금액={}, 트랜잭션 ID={}",
                    refundId, order.getId(), refund.getAmount(), transactionId);

            return convertToDTO(updatedRefund);

        } catch (Exception e) {
            log.error("환불 처리 중 오류: {}", e.getMessage());
            throw new RuntimeException("환불 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public RefundDTO completeRefund(Long refundId, String adminNote) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("환불 요청을 찾을 수 없습니다: " + refundId));

        if (refund.getStatus() != RefundStatus.APPROVED) {
            throw new IllegalStateException("완료할 수 없는 환불 요청 상태입니다. 현재 상태: " + refund.getStatus());
        }

        refund.setStatus(RefundStatus.COMPLETED);
        refund.setAdminNote(adminNote);
        refund.setCompletedAt(LocalDateTime.now());

        Refund updatedRefund = refundRepository.save(refund);

        log.info("환불 완료 처리: 환불 ID={}, 주문 ID={}, 금액={}",
                refundId, refund.getOrder().getId(), refund.getAmount());

        return convertToDTO(updatedRefund);
    }

    @Override
    public List<RefundItemDTO> getRefundItems(Long refundId) {
        List<RefundItem> refundItems = refundItemRepository.findByRefundId(refundId);
        return refundItems.stream()
                .map(this::convertToItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<RefundDTO> getRefundsByStatus(String status, Pageable pageable) {
        RefundStatus refundStatus;
        try {
            refundStatus = RefundStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 환불 상태입니다: " + status);
        }

        Page<Refund> refunds = refundRepository.findByStatus(refundStatus, pageable);
        return refunds.map(this::convertToDTO);
    }

    @Override
    public boolean canRequestRefund(Long orderId) {
        Order order = orderService.getOrderEntityById(orderId);

        // 환불 가능한 주문 상태 확인
        if (order.getStatus().equals(OrderStatus.CANCELLED) ||
                order.getStatus().equals(OrderStatus.REFUNDED)) {
            return false;
        }

        // 주문 날짜 확인 (예: 30일 이내만 환불 가능)
        LocalDate orderDate = order.getCreatedAt().toLocalDate();
        LocalDate now = LocalDate.now();

        // Period를 사용하여 날짜 차이 계산
        Period period = Period.between(orderDate, now);
        int daysBetween = period.getDays() + period.getMonths() * 30 + period.getYears() * 365;

        if (daysBetween > 30) {
            return false;
        }

        return true;
    }

    @Override
    public BigDecimal calculateRefundAmount(List<RefundItemDTO> items, double shippingRefundPercent) {
        BigDecimal itemsTotal = items.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 배송비 환불 계산 (예시)
        BigDecimal shippingRefund = BigDecimal.ZERO;
        if (shippingRefundPercent > 0) {
            // 실제 구현에서는 주문의 배송비를 가져와서 계산
            BigDecimal shippingCost = new BigDecimal("3000"); // 예시 배송비
            shippingRefund = shippingCost.multiply(new BigDecimal(shippingRefundPercent / 100.0));
        }

        return itemsTotal.add(shippingRefund);
    }

    // 헬퍼 메서드들

    private String generateRefundNumber() {
        // 환불 번호 생성 로직 (예: RF + 타임스탬프 + 랜덤 숫자)
        return "RF" + System.currentTimeMillis() + String.format("%04d", (int)(Math.random() * 10000));
    }

    private void saveRefundItems(Refund refund, List<RefundItemDTO> itemDTOs) {
        for (RefundItemDTO itemDTO : itemDTOs) {
            RefundItem item = new RefundItem();
            item.setRefund(refund);
            item.setOrderItemId(itemDTO.getOrderItemId());
            item.setProductId(itemDTO.getProductId());
            item.setProductVariantId(itemDTO.getProductVariantId());
            item.setProductName(itemDTO.getProductName());
            item.setVariantName(itemDTO.getVariantName());
            item.setQuantity(itemDTO.getQuantity());
            item.setPrice(itemDTO.getPrice());
            item.setReason(itemDTO.getReason());

            refundItemRepository.save(item);
        }
    }

    private RefundItemDTO convertToItemDTO(RefundItem item) {
        RefundItemDTO dto = new RefundItemDTO();
        dto.setId(item.getId());
        dto.setRefundId(item.getRefund().getId());
        dto.setOrderItemId(item.getOrderItemId());
        dto.setProductId(item.getProductId());
        dto.setProductVariantId(item.getProductVariantId());
        dto.setProductName(item.getProductName());
        dto.setVariantName(item.getVariantName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setTotalPrice(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
        dto.setReason(item.getReason());
        // 이미지 URL 설정 (있는 경우)
        // dto.setImageUrl(item.getImageUrl());

        return dto;
    }

    private RefundDTO convertToDTO(Refund refund) {
        RefundDTO dto = new RefundDTO();
        dto.setId(refund.getId());
        dto.setOrderId(refund.getOrder().getId());
        dto.setOrderNumber(refund.getOrder().getOrderNumber());
        dto.setMemberId(refund.getMember().getId());
        dto.setMemberEmail(refund.getMember().getEmail());
        dto.setRefundNumber(refund.getRefundNumber());
        dto.setAmount(refund.getAmount());
        dto.setReason(refund.getReason());
        dto.setStatus(refund.getStatus());
        dto.setAdminNotes(refund.getAdminNote());
        dto.setRequestedAt(refund.getRequestedAt());
        dto.setProcessedAt(refund.getProcessedAt());
        dto.setCompletedAt(refund.getCompletedAt());
        dto.setRejectedAt(refund.getRejectedAt());
        dto.setCreatedAt(refund.getCreatedAt());
        dto.setUpdatedAt(refund.getUpdatedAt());

        // 배송비 환불 금액
        dto.setShippingAmount(refund.getShippingAmount());

        // 총 환불 금액 (상품 + 배송비)
        BigDecimal totalAmount = refund.getAmount();
        if (refund.getShippingAmount() != null) {
            totalAmount = totalAmount.add(refund.getShippingAmount());
        }
        dto.setTotalAmount(totalAmount);

        // 트랜잭션 ID
        dto.setTransactionId(refund.getTransactionId());

        // 주문 상태 정보 추가
        dto.setOrderStatus(refund.getOrder().getStatus());

        // 환불 아이템 목록 추가
        List<RefundItem> refundItems = refundItemRepository.findByRefundId(refund.getId());
        if (refundItems != null && !refundItems.isEmpty()) {
            dto.setItems(refundItems.stream()
                    .map(this::convertToItemDTO)
                    .collect(Collectors.toList()));
        } else {
            dto.setItems(new ArrayList<>());
        }

        return dto;
    }
}