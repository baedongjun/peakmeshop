package com.peakmeshop.payment;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KakaoPayClient implements PaymentClient {

    private final PaymentGatewayClient gatewayClient;
    private final String adminKey;
    private final String cid;
    private final String apiUrl;

    // 생성자 수정
    public KakaoPayClient(PaymentGatewayClient gatewayClient, String adminKey, String cid, String apiUrl) {
        this.gatewayClient = gatewayClient;
        this.adminKey = adminKey;
        this.cid = cid;
        this.apiUrl = apiUrl;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + adminKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    @Override
    public Map<String, Object> requestPayment(Map<String, Object> paymentInfo) {
        HttpHeaders headers = getHeaders();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", cid);
        params.add("partner_order_id", (String) paymentInfo.get("orderId"));
        params.add("partner_user_id", (String) paymentInfo.get("memberId"));
        params.add("item_name", (String) paymentInfo.get("itemName"));
        params.add("quantity", String.valueOf(paymentInfo.get("quantity")));
        params.add("total_amount", String.valueOf(paymentInfo.get("amount")));
        params.add("tax_free_amount", "0");
        params.add("approval_url", (String) paymentInfo.get("successUrl"));
        params.add("cancel_url", (String) paymentInfo.get("cancelUrl"));
        params.add("fail_url", (String) paymentInfo.get("failUrl"));

        Map<String, Object> response = gatewayClient.post(
                apiUrl + "/v1/payment/ready",
                headers,
                params,
                Map.class
        );

        if (response != null && response.containsKey("tid")) {
            return response;
        }

        throw new RuntimeException("카카오페이 결제 요청에 실패했습니다.");
    }

    @Override
    public Map<String, Object> verifyPayment(String paymentKey, String orderId, Long amount) {
        HttpHeaders headers = getHeaders();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", cid);
        params.add("tid", paymentKey);
        params.add("partner_order_id", orderId);
        params.add("partner_user_id", "");  // 클라이언트에서 전달받아야 함
        params.add("pg_token", "");  // 클라이언트에서 전달받아야 함

        Map<String, Object> response = gatewayClient.post(
                apiUrl + "/v1/payment/approve",
                headers,
                params,
                Map.class
        );

        if (response != null && response.containsKey("aid")) {
            return response;
        }

        throw new RuntimeException("카카오페이 결제 승인에 실패했습니다.");
    }

    @Override
    public Map<String, Object> cancelPayment(String paymentKey, Map<String, Object> cancelInfo) {
        HttpHeaders headers = getHeaders();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", cid);
        params.add("tid", paymentKey);
        params.add("cancel_amount", String.valueOf(cancelInfo.get("amount")));
        params.add("cancel_tax_free_amount", "0");
        params.add("cancel_vat_amount", "0");

        if (cancelInfo.containsKey("reason")) {
            params.add("cancel_available_amount", (String) cancelInfo.get("reason"));
        }

        Map<String, Object> response = gatewayClient.post(
                apiUrl + "/v1/payment/cancel",
                headers,
                params,
                Map.class
        );

        if (response != null && response.containsKey("aid")) {
            return response;
        }

        throw new RuntimeException("카카오페이 결제 취소에 실패했습니다.");
    }

    @Override
    public Map<String, Object> getPaymentDetails(String paymentKey) {
        HttpHeaders headers = getHeaders();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", cid);
        params.add("tid", paymentKey);

        Map<String, Object> response = gatewayClient.post(
                apiUrl + "/v1/payment/order",
                headers,
                params,
                Map.class
        );

        if (response != null) {
            return response;
        }

        throw new RuntimeException("카카오페이 결제 정보 조회에 실패했습니다.");
    }
}