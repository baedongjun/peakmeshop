package com.peakmeshop.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peakmeshop.dto.MemberDTO;
import com.peakmeshop.dto.NotificationDTO;
import com.peakmeshop.service.MemberService;
import com.peakmeshop.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<Page<NotificationDTO>> getNotifications(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10) Pageable pageable) {

        if (userDetails == null) {
            return ResponseEntity.badRequest().build();
        }

        MemberDTO member = memberService.getMemberByEmail(userDetails.getUsername());
        Page<NotificationDTO> notifications = notificationService.getNotificationsByMemberId(member.getId(), pageable);

        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.badRequest().build();
        }

        MemberDTO member = memberService.getMemberByEmail(userDetails.getUsername());
        List<NotificationDTO> notifications = notificationService.getUnreadNotificationsByMemberId(member.getId());

        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getNotification(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {

        if (userDetails == null) {
            return ResponseEntity.badRequest().build();
        }

        MemberDTO member = memberService.getMemberByEmail(userDetails.getUsername());
        NotificationDTO notification = notificationService.getNotificationByIdAndMemberId(id, member.getId());

        return ResponseEntity.ok(notification);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationDTO> markAsRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {

        if (userDetails == null) {
            return ResponseEntity.badRequest().build();
        }

        MemberDTO member = memberService.getMemberByEmail(userDetails.getUsername());
        NotificationDTO notification = notificationService.markAsRead(id, member.getId());

        return ResponseEntity.ok(notification);
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.badRequest().build();
        }

        MemberDTO member = memberService.getMemberByEmail(userDetails.getUsername());
        notificationService.markAllAsRead(member.getId());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {

        if (userDetails == null) {
            return ResponseEntity.badRequest().build();
        }

        MemberDTO member = memberService.getMemberByEmail(userDetails.getUsername());
        notificationService.deleteNotification(id, member.getId());

        return ResponseEntity.ok().build();
    }
}