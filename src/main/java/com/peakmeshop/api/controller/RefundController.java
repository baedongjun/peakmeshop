package com.peakmeshop.api.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.peakmeshop.api.dto.RefundDTO;
import com.peakmeshop.api.dto.RefundItemDTO;
import com.peakmeshop.api.dto.RefundRequestDTO;
import com.peakmeshop.domain.enums.RefundStatus;
import com.peakmeshop.common.security.oauth2.user.UserPrincipal;
import com.peakmeshop.domain.service.RefundService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
@Slf4j
public class RefundController {

    private final RefundService refundService;

    @PostMapping
    public ResponseEntity<RefundDTO> createRefund(@RequestBody RefundRequestDTO refundRequest) {
        RefundDTO refund = refundService.requestRefund(refundRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(refund);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RefundDTO> getRefundById(@PathVariable Long id) {
        RefundDTO refund = refundService.getRefundById(id);
        return ResponseEntity.ok(refund);
    }

    @GetMapping("/by-member")
    public ResponseEntity<List<RefundDTO>> getRefundsByMember(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<RefundDTO> refunds = refundService.getRefundsByMemberId(userPrincipal.getId());
        return ResponseEntity.ok(refunds);
    }

    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<List<RefundDTO>> getRefundsByOrder(@PathVariable Long orderId,
                                                             @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<RefundDTO> refunds = refundService.getRefundsByOrderId(orderId);

        // 권한 확인: 관리자이거나 본인의 주문인 경우만 조회 가능
        if (userPrincipal != null && !refunds.isEmpty()) {
            boolean isAdmin = userPrincipal.isAdmin();

            if (!isAdmin && !refunds.get(0).getMemberId().equals(userPrincipal.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        return ResponseEntity.ok(refunds);
    }

    @GetMapping("/by-number/{refundNumber}")
    public ResponseEntity<RefundDTO> getRefundByNumber(@PathVariable String refundNumber) {
        RefundDTO refund = refundService.getRefundByRefundNumber(refundNumber);
        return ResponseEntity.ok(refund);
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<RefundDTO>> getRefundsByStatus(@PathVariable String status) {
        try {
            RefundStatus refundStatus = RefundStatus.valueOf(status.toUpperCase());
            List<RefundDTO> refunds = refundService.getRefundsByStatus(refundStatus);
            return ResponseEntity.ok(refunds);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/items/{refundId}")
    public ResponseEntity<List<RefundItemDTO>> getRefundItems(@PathVariable Long refundId) {
        List<RefundItemDTO> items = refundService.getRefundItems(refundId);
        return ResponseEntity.ok(items);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RefundDTO> updateRefund(@PathVariable Long id, @RequestBody RefundDTO refundDTO) {
        // DTO 유효성 검사
        if (refundDTO.getId() == null || !refundDTO.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }

        RefundDTO updatedRefund = refundService.updateRefund(id, refundDTO);
        return ResponseEntity.ok(updatedRefund);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<RefundDTO> updateRefundStatus(@PathVariable Long id,
                                                        @RequestParam String status,
                                                        @RequestParam(required = false) String adminNote) {
        try {
            RefundDTO updatedRefund = refundService.updateRefundStatus(id, status, adminNote);
            return ResponseEntity.ok(updatedRefund);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<RefundDTO> approveRefund(@PathVariable Long id,
                                                   @RequestParam(required = false) String adminNote) {
        try {
            RefundDTO approvedRefund = refundService.approveRefund(id, adminNote);
            return ResponseEntity.ok(approvedRefund);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            log.error("환불 승인 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<RefundDTO> rejectRefund(@PathVariable Long id,
                                                  @RequestParam String adminNote) {
        try {
            RefundDTO rejectedRefund = refundService.rejectRefund(id, adminNote);
            return ResponseEntity.ok(rejectedRefund);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @PutMapping("/{id}/process")
    public ResponseEntity<RefundDTO> processRefund(@PathVariable Long id,
                                                   @RequestParam String transactionId,
                                                   @RequestParam(required = false) String adminNote) {
        try {
            RefundDTO processedRefund = refundService.processRefund(id, transactionId, adminNote);
            return ResponseEntity.ok(processedRefund);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            log.error("환불 처리 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<RefundDTO> completeRefund(@PathVariable Long id,
                                                    @RequestParam(required = false) String adminNote) {
        try {
            RefundDTO completedRefund = refundService.completeRefund(id, adminNote);
            return ResponseEntity.ok(completedRefund);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @GetMapping("/can-request/{orderId}")
    public ResponseEntity<Boolean> canRequestRefund(@PathVariable Long orderId) {
        boolean canRequest = refundService.canRequestRefund(orderId);
        return ResponseEntity.ok(canRequest);
    }

    @PostMapping("/calculate-amount")
    public ResponseEntity<BigDecimal> calculateRefundAmount(@RequestBody List<RefundItemDTO> items,
                                                            @RequestParam(defaultValue = "0") double shippingRefundPercent) {
        BigDecimal amount = refundService.calculateRefundAmount(items, shippingRefundPercent);
        return ResponseEntity.ok(amount);
    }
}