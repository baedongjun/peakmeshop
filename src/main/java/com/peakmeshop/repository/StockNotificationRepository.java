package com.peakmeshop.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peakmeshop.entity.StockNotification;

public interface StockNotificationRepository extends JpaRepository<StockNotification, Long> {

    List<StockNotification> findByMemberIdAndIsActiveTrue(Long memberId);

    List<StockNotification> findByProductIdAndIsActiveTrue(Long productId);

    Optional<StockNotification> findByProductIdAndMemberId(Long productId, Long memberId);

    boolean existsByProductIdAndMemberId(Long productId, Long memberId);

    @Modifying
    @Query("UPDATE StockNotification sn SET sn.isActive = false, sn.notifiedAt = CURRENT_TIMESTAMP " +
            "WHERE sn.product.id = :productId AND sn.isActive = true")
    int markNotificationsAsNotified(@Param("productId") Long productId);

    @Modifying
    @Query("DELETE FROM StockNotification sn WHERE sn.isActive = false AND sn.notifiedAt < :date")
    int deleteOldNotifications(@Param("date") LocalDateTime date);

    @Query("SELECT COUNT(sn) FROM StockNotification sn WHERE sn.product.id = :productId AND sn.isActive = true")
    long countActiveNotificationsByProductId(@Param("productId") Long productId);
}