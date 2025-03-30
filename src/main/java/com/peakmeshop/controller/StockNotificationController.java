package com.peakmeshop.controller;

import com.peakmeshop.dto.StockNotificationDTO;
import com.peakmeshop.security.oauth2.user.UserPrincipal;
import com.peakmeshop.service.StockNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock-notifications")
public class StockNotificationController {

    private final StockNotificationService stockNotificationService;

    public StockNotificationController(StockNotificationService stockNotificationService) {
        this.stockNotificationService = stockNotificationService;
    }

    @PostMapping("/products/{productId}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<?> subscribeToStockNotification(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        boolean result = stockNotificationService.subscribeToStockNotification(productId, currentUser.getId());

        if (result) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "재고 알림 신청이 완료되었습니다. 상품이 입고되면 알림을 보내드립니다."
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "재고 알림 신청에 실패했습니다. 이미 신청하셨거나 상품이 존재하지 않습니다."
            ));
        }
    }

    @DeleteMapping("/products/{productId}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<?> unsubscribeFromStockNotification(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        boolean result = stockNotificationService.unsubscribeFromStockNotification(productId, currentUser.getId());

        if (result) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "재고 알림 신청이 취소되었습니다."
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "재고 알림 신청 취소에 실패했습니다. 신청 내역이 없거나 상품이 존재하지 않습니다."
            ));
        }
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<StockNotificationDTO>> getMyStockNotifications(
            @AuthenticationPrincipal UserPrincipal currentUser) {

        List<StockNotificationDTO> notifications = stockNotificationService.getStockNotificationsByMemberId(currentUser.getId());
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/check/{productId}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<?> checkStockNotificationStatus(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        boolean isSubscribed = stockNotificationService.isSubscribed(productId, currentUser.getId());

        return ResponseEntity.ok(Map.of(
                "subscribed", isSubscribed
        ));
    }

    // 관리자용 API
    @GetMapping("/admin/products/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StockNotificationDTO>> getStockNotificationsByProduct(
            @PathVariable Long productId) {

        List<StockNotificationDTO> notifications = stockNotificationService.getStockNotificationsByProductId(productId);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/admin/notify/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> sendStockNotifications(@PathVariable Long productId) {

        int notifiedCount = stockNotificationService.sendStockNotifications(productId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", notifiedCount + "명의 회원에게 재고 알림이 발송되었습니다."
        ));
    }
}