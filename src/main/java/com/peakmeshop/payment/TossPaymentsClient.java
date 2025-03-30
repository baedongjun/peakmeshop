package com.peakmeshop.payment;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TossPaymentsClient implements PaymentClient {

    private final PaymentGatewayClient gatewayClient;
    private final String secretKey;
    private final String clientKey;
    private final String apiUrl;

    // 생성자 수정
    public TossPaymentsClient(PaymentGatewayClient gatewayClient, String secretKey, String clientKey, String apiUrl) {
        this.gatewayClient = gatewayClient;
        this.secretKey = secretKey;
        this.clientKey = clientKey;
        this.apiUrl = apiUrl;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String encodedKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
        headers.add("Authorization", "Basic " + encodedKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Override
    public Map<String, Object> requestPayment(Map<String, Object> paymentInfo) {
        // 토스페이먼츠는 클라이언트 측에서 결제 요청을 처리하므로 서버에서는 별도 처리 없음
        return paymentInfo;
    }

    @Override
    public Map<String, Object> verifyPayment(String paymentKey, String orderId, Long amount) {
        HttpHeaders headers = getHeaders();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("orderId", orderId);
        requestBody.put("amount", amount);

        Map<String, Object> response = gatewayClient.post(
                apiUrl + "/v1/payments/" + paymentKey,
                headers,
                requestBody,
                Map.class
        );

        if (response != null && response.containsKey("paymentKey")) {
            return response;
        }

        throw new RuntimeException("토스페이먼츠 결제 승인에 실패했습니다.");
    }

    @Override
    public Map<String, Object> cancelPayment(String paymentKey, Map<String, Object> cancelInfo) {
        HttpHeaders headers = getHeaders();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("cancelReason", cancelInfo.getOrDefault("reason", "취소 요청"));

        if (cancelInfo.containsKey("cancelAmount")) {
            requestBody.put("cancelAmount", cancelInfo.get("cancelAmount"));
        }

        Map<String, Object> response = gatewayClient.post(
                apiUrl + "/v1/payments/" + paymentKey + "/cancel",
                headers,
                requestBody,
                Map.class
        );

        if (response != null && response.containsKey("paymentKey")) {
            return response;
        }

        throw new RuntimeException("토스페이먼츠 결제 취소에 실패했습니다.");
    }

    @Override
    public Map<String, Object> getPaymentDetails(String paymentKey) {
        HttpHeaders headers = getHeaders();

        Map<String, Object> response = gatewayClient.get(
                apiUrl + "/v1/payments/" + paymentKey,
                headers,
                Map.class
        );

        if (response != null) {
            return response;
        }

        throw new RuntimeException("토스페이먼츠 결제 정보 조회에 실패했습니다.");
    }
}