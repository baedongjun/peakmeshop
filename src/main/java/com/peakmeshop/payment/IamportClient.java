package com.peakmeshop.payment;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class IamportClient implements PaymentClient {

    private final PaymentGatewayClient gatewayClient;
    private final String apiKey;
    private final String apiSecret;
    private final String apiUrl;

    // 생성자 수정
    public IamportClient(PaymentGatewayClient gatewayClient, String apiKey, String apiSecret, String apiUrl) {
        this.gatewayClient = gatewayClient;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.apiUrl = apiUrl;
    }

    private String getToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("imp_key", apiKey);
        requestBody.put("imp_secret", apiSecret);

        Map<String, Object> response = gatewayClient.post(
                apiUrl + "/users/getToken",
                headers,
                requestBody,
                Map.class
        );

        if (response != null && response.containsKey("response")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> responseData = (Map<String, Object>) response.get("response");
            return (String) responseData.get("access_token");
        }

        throw new RuntimeException("아임포트 토큰 발급에 실패했습니다.");
    }

    @Override
    public Map<String, Object> requestPayment(Map<String, Object> paymentInfo) {
        // 아임포트는 클라이언트 측에서 결제 요청을 처리하므로 서버에서는 별도 처리 없음
        return paymentInfo;
    }

    @Override
    public Map<String, Object> verifyPayment(String paymentKey, String orderId, Long amount) {
        String token = getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> response = gatewayClient.get(
                apiUrl + "/payments/" + paymentKey,
                headers,
                Map.class
        );

        if (response != null && response.containsKey("response")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> payment = (Map<String, Object>) response.get("response");

            // 결제 검증
            String responseOrderId = (String) payment.get("merchant_uid");
            Long responseAmount = ((Number) payment.get("amount")).longValue();

            if (!orderId.equals(responseOrderId) || !amount.equals(responseAmount)) {
                throw new RuntimeException("결제 정보가 일치하지 않습니다.");
            }

            return payment;
        }

        throw new RuntimeException("결제 검증에 실패했습니다.");
    }

    @Override
    public Map<String, Object> cancelPayment(String paymentKey, Map<String, Object> cancelInfo) {
        String token = getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("imp_uid", paymentKey);
        requestBody.put("reason", cancelInfo.getOrDefault("reason", "취소 요청"));

        if (cancelInfo.containsKey("amount")) {
            requestBody.put("amount", cancelInfo.get("amount"));
        }

        Map<String, Object> response = gatewayClient.post(
                apiUrl + "/payments/cancel",
                headers,
                requestBody,
                Map.class
        );

        if (response != null && response.containsKey("response")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> cancelData = (Map<String, Object>) response.get("response");
            return cancelData;
        }

        throw new RuntimeException("결제 취소에 실패했습니다.");
    }

    @Override
    public Map<String, Object> getPaymentDetails(String paymentKey) {
        String token = getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> response = gatewayClient.get(
                apiUrl + "/payments/" + paymentKey,
                headers,
                Map.class
        );

        if (response != null && response.containsKey("response")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> payment = (Map<String, Object>) response.get("response");
            return payment;
        }

        throw new RuntimeException("결제 정보 조회에 실패했습니다.");
    }
}