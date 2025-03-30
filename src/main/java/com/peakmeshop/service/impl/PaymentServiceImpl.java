package com.peakmeshop.service.impl;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.peakmeshop.payment.IamportClient;
import com.peakmeshop.payment.KakaoPayClient;
import com.peakmeshop.payment.PaymentClient;
import com.peakmeshop.payment.TossPaymentsClient;
import com.peakmeshop.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final IamportClient iamportClient;
    private final KakaoPayClient kakaoPayClient;
    private final TossPaymentsClient tossPaymentsClient;

    @Override
    public Map<String, Object> requestPayment(String paymentMethod, Map<String, Object> paymentInfo) {
        PaymentClient client = getPaymentClient(paymentMethod);
        return client.requestPayment(paymentInfo);
    }

    @Override
    public Map<String, Object> verifyPayment(String paymentMethod, String paymentKey, String orderId, Long amount) {
        PaymentClient client = getPaymentClient(paymentMethod);
        return client.verifyPayment(paymentKey, orderId, amount);
    }

    @Override
    public Map<String, Object> cancelPayment(String paymentMethod, String paymentKey, Map<String, Object> cancelInfo) {
        PaymentClient client = getPaymentClient(paymentMethod);
        return client.cancelPayment(paymentKey, cancelInfo);
    }

    @Override
    public Map<String, Object> getPaymentDetails(String paymentMethod, String paymentKey) {
        PaymentClient client = getPaymentClient(paymentMethod);
        return client.getPaymentDetails(paymentKey);
    }

    private PaymentClient getPaymentClient(String paymentMethod) {
        switch (paymentMethod.toLowerCase()) {
            case "iamport":
                return iamportClient;
            case "kakaopay":
                return kakaoPayClient;
            case "tosspayments":
                return tossPaymentsClient;
            default:
                throw new IllegalArgumentException("지원하지 않는 결제 방식입니다: " + paymentMethod);
        }
    }

    @Override
    public boolean processPayment(String paymentMethod, BigDecimal amount, String description) {
        // 실제 결제 처리 로직 구현
        // 여기서는 항상 성공한다고 가정
        log.info("결제 처리: 방식={}, 금액={}, 설명={}", paymentMethod, amount, description);
        return true;
    }

    @Override
    public boolean processRefund(String paymentReference, BigDecimal amount) {
        // 실제 환불 처리 로직 구현
        // 여기서는 항상 성공한다고 가정
        log.info("환불 처리: 결제 참조={}, 금액={}", paymentReference, amount);
        return true;
    }

    @Override
    public String getPaymentStatus(String paymentId) {
        // 실제 결제 상태 조회 로직 구현
        // 여기서는 항상 "COMPLETED"를 반환한다고 가정
        log.info("결제 상태 조회: 결제 ID={}", paymentId);
        return "COMPLETED";
    }
}