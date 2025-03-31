package com.peakmeshop.domain.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.RefundDTO;
import com.peakmeshop.api.dto.RefundItemDTO;
import com.peakmeshop.api.dto.RefundRequestDTO;
import com.peakmeshop.domain.enums.RefundStatus;

public interface RefundService {

    // 기존 메서드
    RefundDTO requestRefund(RefundRequestDTO refundRequest);
    RefundDTO approveRefund(Long refundId, String adminNote);
    RefundDTO rejectRefund(Long refundId, String adminNote);
    List<RefundDTO> getAllRefunds();
    List<RefundDTO> getRefundsByOrderId(Long orderId);
    List<RefundDTO> getRefundsByMemberId(Long memberId);
    RefundDTO getRefundById(Long refundId);
    List<RefundDTO> getRefundsByStatus(RefundStatus status);

    // 컨트롤러에서 필요한 추가 메서드
    Page<RefundDTO> getRefundsByMemberId(Long memberId, Pageable pageable);
    RefundDTO getRefundByRefundNumber(String refundNumber);
    RefundDTO createRefund(RefundDTO refundDTO);
    RefundDTO updateRefund(Long refundId, RefundDTO refundDTO);
    RefundDTO updateRefundStatus(Long refundId, String status, String adminNote);
    RefundDTO processRefund(Long refundId, String transactionId, String adminNote);
    RefundDTO completeRefund(Long refundId, String adminNote);
    List<RefundItemDTO> getRefundItems(Long refundId);
    Page<RefundDTO> getRefundsByStatus(String status, Pageable pageable);
    boolean canRequestRefund(Long orderId);
    BigDecimal calculateRefundAmount(List<RefundItemDTO> items, double shippingRefundPercent);
}