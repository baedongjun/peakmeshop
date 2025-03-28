package com.peakmeshop.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.dto.NotificationDTO;

public interface NotificationService {

    /**
     * 알림 생성
     * @param memberId 회원 ID
     * @param title 제목
     * @param message 내용
     * @param type 알림 유형
     * @param link 링크 (선택적)
     * @return 생성된 알림
     */
    NotificationDTO createNotification(Long memberId, String title, String message, String type, String link);

    /**
     * 회원의 알림 목록 조회
     * @param memberId 회원 ID
     * @param pageable 페이징 정보
     * @return 알림 목록
     */
    Page<NotificationDTO> getNotifications(Long memberId, Pageable pageable);

    /**
     * 회원의 읽지 않은 알림 목록 조회
     * @param memberId 회원 ID
     * @return 읽지 않은 알림 목록
     */
    List<NotificationDTO> getUnreadNotifications(Long memberId);

    /**
     * 알림 읽음 처리
     * @param notificationId 알림 ID
     * @param memberId 회원 ID
     * @return 업데이트된 알림
     */
    NotificationDTO markAsRead(Long notificationId, Long memberId);

    /**
     * 모든 알림 읽음 처리
     * @param memberId 회원 ID
     */
    void markAllAsRead(Long memberId);

    /**
     * 알림 삭제
     * @param notificationId 알림 ID
     * @param memberId 회원 ID
     */
    void deleteNotification(Long notificationId, Long memberId);

    /**
     * 읽은 알림 모두 삭제
     * @param memberId 회원 ID
     */
    void deleteAllReadNotifications(Long memberId);

    /**
     * 읽지 않은 알림 수 조회
     * @param memberId 회원 ID
     * @return 읽지 않은 알림 수
     */
    int countUnreadNotifications(Long memberId);
}