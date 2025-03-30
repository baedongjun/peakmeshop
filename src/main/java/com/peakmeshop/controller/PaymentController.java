package com.peakmeshop.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peakmeshop.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{paymentMethod}/request")
    public ResponseEntity<?> requestPayment(
            @PathVariable String paymentMethod,
            @RequestBody Map<String, Object> paymentInfo) {

        Map<String, Object> response = paymentService.requestPayment(paymentMethod, paymentInfo);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{paymentMethod}/verify")
    public ResponseEntity<?> verifyPayment(
            @PathVariable String paymentMethod,
            @RequestBody Map<String, Object> verifyInfo) {

        String paymentKey = (String) verifyInfo.get("paymentKey");
        String orderId = (String) verifyInfo.get("orderId");
        Long amount = Long.valueOf(verifyInfo.get("amount").toString());

        Map<String, Object> response = paymentService.verifyPayment(paymentMethod, paymentKey, orderId, amount);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{paymentMethod}/cancel")
    public ResponseEntity<?> cancelPayment(
            @PathVariable String paymentMethod,
            @RequestParam String paymentKey,
            @RequestBody Map<String, Object> cancelInfo) {

        Map<String, Object> response = paymentService.cancelPayment(paymentMethod, paymentKey, cancelInfo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{paymentMethod}/details")
    public ResponseEntity<?> getPaymentDetails(
            @PathVariable String paymentMethod,
            @RequestParam String paymentKey) {

        Map<String, Object> response = paymentService.getPaymentDetails(paymentMethod, paymentKey);
        return ResponseEntity.ok(response);
    }

    // 카카오페이 콜백 처리
    @GetMapping("/kakaopay/success")
    public ResponseEntity<?> kakaoPaySuccess(
            @RequestParam String pg_token,
            @RequestParam String partner_order_id) {

        // 프론트엔드로 리다이렉트 또는 결과 반환
        return ResponseEntity.ok(Map.of(
                "success", true,
                "pgToken", pg_token,
                "orderId", partner_order_id
        ));
    }

    @GetMapping("/kakaopay/cancel")
    public ResponseEntity<?> kakaoPayCancel() {
        return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "결제가 취소되었습니다."
        ));
    }

    @GetMapping("/kakaopay/fail")
    public ResponseEntity<?> kakaoPayFail() {
        return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "결제에 실패했습니다."
        ));
    }
}