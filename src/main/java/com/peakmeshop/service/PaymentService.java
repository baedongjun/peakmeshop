package com.peakmeshop.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peakmeshop.dto.PaymentRequestDTO;
import com.peakmeshop.dto.PaymentResponseDTO;
import com.peakmeshop.exception.PaymentException;

@Service
public class PaymentService {

    @Value("${app.payment.imp-key}")
    private String impKey;

    @Value("${app.payment.imp-secret}")
    private String impSecret;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    // 결제 토큰 발급
    private String getAccessToken() throws IOException, InterruptedException {
        String requestBody = objectMapper.writeValueAsString(Map.of(
                "imp_key", impKey,
                "imp_secret", impSecret
        ));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.iamport.kr/users/getToken"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
        Map<String, Object> responseData = (Map<String, Object>) responseMap.get("response");

        return (String) responseData.get("access_token");
    }

    // 결제 정보 조회
    public PaymentResponseDTO getPaymentInfo(String impUid) {
        try {
            String accessToken = getAccessToken();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.iamport.kr/payments/" + impUid))
                    .header("Authorization", "Bearer " + accessToken)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
            Map<String, Object> responseData = (Map<String, Object>) responseMap.get("response");

            // 결제 금액 검증 등의 로직 추가

            return new PaymentResponseDTO(
                    (String) responseData.get("imp_uid"),
                    (String) responseData.get("merchant_uid"),
                    new BigDecimal(responseData.get("amount").toString()),
                    (String) responseData.get("status")
            );

        } catch (Exception e) {
            throw new PaymentException("결제 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 결제 취소
    public void cancelPayment(String impUid, String reason) {
        try {
            String accessToken = getAccessToken();

            String requestBody = objectMapper.writeValueAsString(Map.of(
                    "imp_uid", impUid,
                    "reason", reason
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.iamport.kr/payments/cancel"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            throw new PaymentException("결제 취소 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}