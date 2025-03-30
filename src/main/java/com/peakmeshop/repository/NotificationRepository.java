package com.peakmeshop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 회원 ID로 읽지 않은 알림 조회 (생성일 내림차순)
    List<Notification> findByMemberIdAndReadFalseOrderByCreatedAtDesc(Long memberId);

    // 회원 ID로 모든 알림 조회 (생성일 내림차순, 페이징)
    Page<Notification> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    // 회원 ID로 모든 알림 조회 (페이징)
    Page<Notification> findByMemberId(Long memberId, Pageable pageable);

    // 회원 ID로 읽지 않은 알림 조회 (페이징)
    Page<Notification> findByMemberIdAndReadFalse(Long memberId, Pageable pageable);

    // 알림 ID와 회원 ID로 알림 조회
    Optional<Notification> findByIdAndMemberId(Long id, Long memberId);

    // 회원 ID로 읽지 않은 알림 개수 조회
    int countByMemberIdAndReadFalse(Long memberId);
}