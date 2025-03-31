package com.peakmeshop.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.peakmeshop.payment.IamportClient;
import com.peakmeshop.payment.KakaoPayClient;
import com.peakmeshop.payment.PaymentGatewayClient;
import com.peakmeshop.payment.TossPaymentsClient;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class PaymentGatewayConfig {

    private final RestTemplate restTemplate;

    @Value("${payment.iamport.api-key}")
    private String iamportApiKey;

    @Value("${payment.iamport.api-secret}")
    private String iamportApiSecret;

    @Value("${payment.iamport.api-url}")
    private String iamportApiUrl;

    @Value("${payment.kakaopay.admin-key}")
    private String kakaoPayAdminKey;

    @Value("${payment.kakaopay.cid}")
    private String kakaoPayCid;

    @Value("${payment.kakaopay.api-url}")
    private String kakaoPayApiUrl;

    @Value("${payment.toss.secret-key}")
    private String tossSecretKey;

    @Value("${payment.toss.client-key}")
    private String tossClientKey;

    @Value("${payment.toss.api-url}")
    private String tossApiUrl;

    @Bean
    public PaymentGatewayClient paymentGatewayClient() {
        // 수정된 부분: PaymentGatewayClient 생성자에 RestTemplate 전달
        return new PaymentGatewayClient(restTemplate);
    }

    @Bean
    public IamportClient iamportClient() {
        // 수정된 부분: IamportClient 생성자에 PaymentGatewayClient 전달
        return new IamportClient(paymentGatewayClient(), iamportApiKey, iamportApiSecret, iamportApiUrl);
    }

    @Bean
    public KakaoPayClient kakaoPayClient() {
        // 수정된 부분: KakaoPayClient 생성자에 PaymentGatewayClient 전달
        return new KakaoPayClient(paymentGatewayClient(), kakaoPayAdminKey, kakaoPayCid, kakaoPayApiUrl);
    }

    @Bean
    public TossPaymentsClient tossPaymentsClient() {
        // 수정된 부분: TossPaymentsClient 생성자에 PaymentGatewayClient 전달
        return new TossPaymentsClient(paymentGatewayClient(), tossSecretKey, tossClientKey, tossApiUrl);
    }
}