package com.peakmeshop.payment;

import java.util.Map;

public interface PaymentClient {

    Map<String, Object> requestPayment(Map<String, Object> paymentInfo);

    Map<String, Object> verifyPayment(String paymentKey, String orderId, Long amount);

    Map<String, Object> cancelPayment(String paymentKey, Map<String, Object> cancelInfo);

    Map<String, Object> getPaymentDetails(String paymentKey);
}