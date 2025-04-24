package com.peakmeshop.domain.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.NotificationDTO;
import com.peakmeshop.domain.entity.Member;
import com.peakmeshop.domain.entity.Notification;
import com.peakmeshop.domain.repository.MemberRepository;
import com.peakmeshop.domain.repository.NotificationRepository;
import com.peakmeshop.domain.service.NotificationService;
import com.peakmeshop.api.mapper.NotificationMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional(readOnly = true)
    public NotificationDTO getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id: " + id));

        return notificationMapper.toDTO(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDTO> getNotificationsByMemberId(Long memberId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);
        return notifications.map(notificationMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotificationsByMemberId(Long memberId) {
        List<Notification> notifications = notificationRepository.findByMemberIdAndReadFalseOrderByCreatedAtDesc(memberId);
        return notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationDTO getNotificationByIdAndMemberId(Long id, Long memberId) {
        Notification notification = notificationRepository.findByIdAndMemberId(id, memberId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id: " + id + " for member: " + memberId));

        return notificationMapper.toDTO(notification);
    }

    @Override
    @Transactional
    public NotificationDTO markAsRead(Long id, Long memberId) {
        Notification notification = notificationRepository.findByIdAndMemberId(id, memberId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id: " + id + " for member: " + memberId));

        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());

        Notification updatedNotification = notificationRepository.save(notification);
        return notificationMapper.toDTO(updatedNotification);
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
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id: " + id + " for member: " + memberId));

        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public NotificationDTO createNotification(NotificationDTO notificationDTO) {
        Member member = memberRepository.findById(notificationDTO.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + notificationDTO.getMemberId()));

        Notification notification = notificationMapper.toEntity(notificationDTO);
        notification.setMember(member);
        notification.setCreatedAt(LocalDateTime.now());

        Notification savedNotification = notificationRepository.save(notification);
        return notificationMapper.toDTO(savedNotification);
    }

    @Override
    @Transactional(readOnly = true)
    public int getUnreadCount(Long memberId) {
        return notificationRepository.countByMemberIdAndReadFalse(memberId);
    }
}