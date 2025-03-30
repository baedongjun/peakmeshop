package com.peakmeshop.service;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentService {

    Map<String, Object> requestPayment(String paymentMethod, Map<String, Object> paymentInfo);

    Map<String, Object> verifyPayment(String paymentMethod, String paymentKey, String orderId, Long amount);

    Map<String, Object> cancelPayment(String paymentMethod, String paymentKey, Map<String, Object> cancelInfo);

    Map<String, Object> getPaymentDetails(String paymentMethod, String paymentKey);

    boolean processPayment(String paymentMethod, BigDecimal amount, String description);

    boolean processRefund(String paymentReference, BigDecimal amount);

    String getPaymentStatus(String paymentId);
}