package com.peakmeshop.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.dto.NotificationDTO;

public interface NotificationService {

    NotificationDTO getNotificationById(Long id);

    Page<NotificationDTO> getNotificationsByMemberId(Long memberId, Pageable pageable);

    List<NotificationDTO> getUnreadNotificationsByMemberId(Long memberId);

    NotificationDTO getNotificationByIdAndMemberId(Long id, Long memberId);

    NotificationDTO markAsRead(Long id, Long memberId);

    void markAllAsRead(Long memberId);

    void deleteNotification(Long id, Long memberId);

    NotificationDTO createNotification(NotificationDTO notificationDTO);

    int getUnreadCount(Long memberId);
}