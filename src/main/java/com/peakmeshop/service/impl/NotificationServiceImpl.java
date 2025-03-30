package com.peakmeshop.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.dto.NotificationDTO;
import com.peakmeshop.entity.Member;
import com.peakmeshop.entity.Notification;
import com.peakmeshop.exception.ResourceNotFoundException;
import com.peakmeshop.repository.MemberRepository;
import com.peakmeshop.repository.NotificationRepository;
import com.peakmeshop.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public NotificationDTO getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        return convertToDTO(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDTO> getNotificationsByMemberId(Long memberId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);
        return notifications.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotificationsByMemberId(Long memberId) {
        List<Notification> notifications = notificationRepository.findByMemberIdAndReadFalseOrderByCreatedAtDesc(memberId);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationDTO getNotificationByIdAndMemberId(Long id, Long memberId) {
        Notification notification = notificationRepository.findByIdAndMemberId(id, memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id + " for member: " + memberId));

        return convertToDTO(notification);
    }

    @Override
    @Transactional
    public NotificationDTO markAsRead(Long id, Long memberId) {
        Notification notification = notificationRepository.findByIdAndMemberId(id, memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id + " for member: " + memberId));

        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());

        Notification updatedNotification = notificationRepository.save(notification);
        return convertToDTO(updatedNotification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long memberId) {
        List<Notification> unreadNotifications = notificationRepository.findByMemberIdAndReadFalseOrderByCreatedAtDesc(memberId);

        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
        }

        notificationRepository.saveAll(unreadNotifications);
    }

    @Override
    @Transactional
    public void deleteNotification(Long id, Long memberId) {
        Notification notification = notificationRepository.findByIdAndMemberId(id, memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id + " for member: " + memberId));

        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public NotificationDTO createNotification(NotificationDTO notificationDTO) {
        Member member = memberRepository.findById(notificationDTO.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + notificationDTO.getMemberId()));

        Notification notification = Notification.builder()
                .member(member)
                .title(notificationDTO.getTitle())
                .message(notificationDTO.getMessage())
                .type(notificationDTO.getType())
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        return convertToDTO(savedNotification);
    }

    @Override
    @Transactional(readOnly = true)
    public int getUnreadCount(Long memberId) {
        return notificationRepository.countByMemberIdAndReadFalse(memberId);
    }

    private NotificationDTO convertToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .memberId(notification.getMember().getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .build();
    }
}