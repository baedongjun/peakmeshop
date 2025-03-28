package com.peakmeshop.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.dto.OrderDTO;
import com.peakmeshop.dto.OrderItemDTO;
import com.peakmeshop.dto.OrderSummaryDTO;

public interface OrderService {

    /**
     * 주문 생성
     * @param memberId 회원 ID
     * @param items 주문 상품 목록
     * @param shippingAddress 배송 주소
     * @param shippingDetailAddress 배송 상세 주소
     * @param shippingZipCode 배송 우편번호
     * @param recipientName 수령인 이름
     * @param recipientPhone 수령인 연락처
     * @param paymentMethod 결제 방법
     * @param impUid 아임포트 결제 고유 ID
     * @return 생성된 주문 정보
     */
    OrderDTO createOrder(Long memberId, List<OrderItemDTO> items, String shippingAddress,
                         String shippingDetailAddress, String shippingZipCode, String recipientName,
                         String recipientPhone, String paymentMethod, String impUid);

    /**
     * 주문 ID로 주문 조회
     * @param id 주문 ID
     * @return 주문 정보
     */
    OrderDTO getOrderById(Long id);

    /**
     * 주문 번호로 주문 조회
     * @param orderNumber 주문 번호
     * @return 주문 정보
     */
    OrderDTO getOrderByOrderNumber(String orderNumber);

    /**
     * 회원 ID로 주문 목록 조회
     * @param memberId 회원 ID
     * @param pageable 페이징 정보
     * @return 주문 목록
     */
    Page<OrderSummaryDTO> getOrdersByMemberId(Long memberId, Pageable pageable);

    /**
     * 모든 주문 목록 조회 (관리자용)
     * @param status 주문 상태 (선택적)
     * @param pageable 페이징 정보
     * @return 주문 목록
     */
    Page<OrderSummaryDTO> getAllOrders(String status, Pageable pageable);

    /**
     * 주문 상태 업데이트
     * @param id 주문 ID
     * @param status 변경할 상태
     * @param reason 변경 사유 (선택적)
     * @return 업데이트된 주문 정보
     */
    OrderDTO updateOrderStatus(Long id, String status, String reason);

    /**
     * 주문 취소
     * @param id 주문 ID
     * @param reason 취소 사유
     * @return 취소된 주문 정보
     */
    OrderDTO cancelOrder(Long id, String reason);

    /**
     * 방치된 장바구니 정리 (스케줄러용)
     */
    void cleanupAbandonedCarts();

    /**
     * 주간 보고서 생성 (스케줄러용)
     */
    void generateWeeklyReport();
}