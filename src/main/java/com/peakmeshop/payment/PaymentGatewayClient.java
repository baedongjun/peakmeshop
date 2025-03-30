package com.peakmeshop.payment;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PaymentGatewayClient {

    private final RestTemplate restTemplate;

    // 생성자 추가
    public PaymentGatewayClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * GET 요청 수행
     *
     * @param url 요청 URL
     * @param headers 요청 헤더
     * @param responseType 응답 타입
     * @return 응답 객체
     */
    public <T> T get(String url, HttpHeaders headers, Class<T> responseType) {
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
        return response.getBody();
    }

    /**
     * POST 요청 수행
     *
     * @param url 요청 URL
     * @param headers 요청 헤더
     * @param body 요청 바디
     * @param responseType 응답 타입
     * @return 응답 객체
     */
    public <T> T post(String url, HttpHeaders headers, Object body, Class<T> responseType) {
        HttpEntity<?> entity = new HttpEntity<>(body, headers);
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
        return response.getBody();
    }

    /**
     * PUT 요청 수행
     *
     * @param url 요청 URL
     * @param headers 요청 헤더
     * @param body 요청 바디
     * @param responseType 응답 타입
     * @return 응답 객체
     */
    public <T> T put(String url, HttpHeaders headers, Object body, Class<T> responseType) {
        HttpEntity<?> entity = new HttpEntity<>(body, headers);
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.PUT, entity, responseType);
        return response.getBody();
    }

    /**
     * DELETE 요청 수행
     *
     * @param url 요청 URL
     * @param headers 요청 헤더
     * @param responseType 응답 타입
     * @return 응답 객체
     */
    public <T> T delete(String url, HttpHeaders headers, Class<T> responseType) {
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, responseType);
        return response.getBody();
    }
}