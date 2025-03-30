package com.peakmeshop.service;

import java.util.List;
import java.util.Optional;

import com.peakmeshop.dto.OrderStatusDTO;

public interface OrderStatusService {

    /**
     * 주문 상태 생성
     * @param orderStatusDTO 주문 상태 정보
     * @return 생성된 주문 상태
     */
    OrderStatusDTO createOrderStatus(OrderStatusDTO orderStatusDTO);

    /**
     * 주문 상태 ID로 조회
     * @param id 주문 상태 ID
     * @return 주문 상태 정보
     */
    Optional<OrderStatusDTO> getOrderStatusById(Long id);

    /**
     * 주문 ID로 주문 상태 목록 조회
     * @param orderId 주문 ID
     * @return 주문 상태 목록
     */
    List<OrderStatusDTO> getOrderStatusesByOrderId(Long orderId);

    /**
     * 주문 번호로 주문 상태 목록 조회
     * @param orderNumber 주문 번호
     * @return 주문 상태 목록
     */
    List<OrderStatusDTO> getOrderStatusesByOrderNumber(String orderNumber);

    /**
     * 주문 ID로 최신 주문 상태 조회
     * @param orderId 주문 ID
     * @return 최신 주문 상태
     */
    Optional<OrderStatusDTO> getLatestOrderStatusByOrderId(Long orderId);

    /**
     * 주문 번호로 최신 주문 상태 조회
     * @param orderNumber 주문 번호
     * @return 최신 주문 상태
     */
    Optional<OrderStatusDTO> getLatestOrderStatusByOrderNumber(String orderNumber);

    /**
     * 주문 상태 업데이트
     * @param orderId 주문 ID
     * @param status 상태
     * @param description 설명
     * @param updatedBy 업데이트한 사용자
     * @return 생성된 주문 상태
     */
    OrderStatusDTO updateOrderStatus(Long orderId, String status, String description, String updatedBy);

    /**
     * 배송 정보 업데이트
     * @param orderId 주문 ID
     * @param trackingNumber 운송장 번호
     * @param trackingUrl 배송 추적 URL
     * @param carrierName 배송사 이름
     * @param updatedBy 업데이트한 사용자
     * @return 생성된 주문 상태
     */
    OrderStatusDTO updateShippingInfo(Long orderId, String trackingNumber, String trackingUrl, String carrierName, String updatedBy);

    /**
     * 주문 취소
     * @param orderId 주문 ID
     * @param reason 취소 사유
     * @param updatedBy 업데이트한 사용자
     * @return 생성된 주문 상태
     */
    OrderStatusDTO cancelOrder(Long orderId, String reason, String updatedBy);

    /**
     * 주문 환불
     * @param orderId 주문 ID
     * @param reason 환불 사유
     * @param updatedBy 업데이트한 사용자
     * @return 생성된 주문 상태
     */
    OrderStatusDTO refundOrder(Long orderId, String reason, String updatedBy);
}