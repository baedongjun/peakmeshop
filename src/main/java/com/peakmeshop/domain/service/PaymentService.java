package com.peakmeshop.domain.service;

import java.math.BigDecimal;
import java.util.Map;
import com.peakmeshop.api.dto.PaymentDTO;

public interface PaymentService {

    PaymentDTO requestPayment(String paymentMethod, Map<String, Object> paymentInfo);

    PaymentDTO verifyPayment(String paymentMethod, String paymentKey, String orderId, Long amount);

    PaymentDTO cancelPayment(String paymentMethod, String paymentKey, Map<String, Object> cancelInfo);

    PaymentDTO getPaymentDetails(String paymentMethod, String paymentKey);

    boolean processPayment(String paymentMethod, BigDecimal amount, String description);

    boolean processRefund(String paymentReference, BigDecimal amount);

    String getPaymentStatus(String paymentId);
}