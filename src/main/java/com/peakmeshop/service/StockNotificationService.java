package com.peakmeshop.service;

import java.util.List;

import com.peakmeshop.dto.StockNotificationDTO;

public interface StockNotificationService {

    boolean subscribeToStockNotification(Long productId, Long memberId);

    boolean unsubscribeFromStockNotification(Long productId, Long memberId);

    List<StockNotificationDTO> getStockNotificationsByMemberId(Long memberId);

    List<StockNotificationDTO> getStockNotificationsByProductId(Long productId);

    boolean isSubscribed(Long productId, Long memberId);

    int sendStockNotifications(Long productId);

    void processStockNotificationsForProduct(Long productId);

    void cleanupOldNotifications();
}