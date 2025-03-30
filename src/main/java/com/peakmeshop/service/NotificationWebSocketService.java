package com.peakmeshop.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.peakmeshop.dto.NotificationDTO;

import java.util.Map;

@Service
public class NotificationWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationWebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotificationToUser(Long userId, NotificationDTO notification) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                notification);
    }

    public void sendNotificationToAll(NotificationDTO notification) {
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }

    public void sendOrderStatusUpdate(Long userId, Long orderId, String status) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/orders",
                Map.of("orderId", orderId, "status", status));
    }

    public void sendStockUpdate(Long productId, int newStock) {
        messagingTemplate.convertAndSend(
                "/topic/products/" + productId + "/stock",
                Map.of("productId", productId, "stock", newStock));
    }
}