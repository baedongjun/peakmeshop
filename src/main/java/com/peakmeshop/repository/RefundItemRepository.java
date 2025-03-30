package com.peakmeshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peakmeshop.entity.RefundItem;

public interface RefundItemRepository extends JpaRepository<RefundItem, Long> {

    List<RefundItem> findByRefundId(Long refundId);

    @Modifying
    @Query("DELETE FROM RefundItem ri WHERE ri.refund.id = :refundId")
    void deleteByRefundId(@Param("refundId") Long refundId);
}