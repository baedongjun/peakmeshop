package com.peakmeshop.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.domain.entity.Refund;
import com.peakmeshop.domain.enums.RefundStatus;

public interface RefundRepository extends JpaRepository<Refund, Long> {

    List<Refund> findByOrderId(Long orderId);

    List<Refund> findByMemberId(Long memberId);

    Page<Refund> findByMemberId(Long memberId, Pageable pageable);

    List<Refund> findByStatus(RefundStatus status);

    Page<Refund> findByStatus(RefundStatus status, Pageable pageable);

    Optional<Refund> findByRefundNumber(String refundNumber);
}