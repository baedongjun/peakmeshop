package com.peakmeshop.domain.service;

import java.util.Map;

import com.peakmeshop.api.dto.EmailDTO;

public interface EmailService {

    // 기본 이메일 전송
    void sendEmail(EmailDTO emailDTO);

    // 문자열 파라미터로 이메일 전송 (오버로딩)
    void sendEmail(String to, String subject, String content);

    // 단순 이메일 전송 (컨트롤러에서 사용)
    void sendSimpleEmail(String to, String subject, String content);

    // 템플릿 이메일 전송 (컨트롤러에서 사용)
    void sendTemplatedEmail(String to, String subject, String templateName, Map<String, Object> templateModel);

    // 회원가입 확인 이메일
    void sendSignupConfirmationEmail(String to, String username);

    // 이메일 인증 이메일 (AuthServiceImpl에서 사용) - 3개 인자
    void sendVerificationEmail(String to, String username, String verificationToken);

    // 이메일 인증 이메일 (MemberServiceImpl에서 사용) - 2개 인자
    void sendVerificationEmail(String to, String verificationToken);

    // 비밀번호 재설정 이메일
    void sendPasswordResetEmail(String to, String resetToken);

    // 비밀번호 재설정 요청 이메일 (AuthServiceImpl에서 사용) - 3개 인자
    void sendPasswordResetRequestEmail(String to, String username, String resetToken);

    // 비밀번호 재설정 요청 이메일 (MemberServiceImpl에서 사용) - 2개 인자
    void sendPasswordResetRequestEmail(String to, String resetToken);

    // 주문 확인 이메일
    void sendOrderConfirmationEmail(String email, Long orderId, String orderNumber);

    // 배송 알림 이메일
    void sendShippingNotificationEmail(String email, Long orderId, String orderNumber, String trackingNumber);

    // 쿠폰 발급 이메일
    void sendCouponEmail(String to, String couponCode, String couponName);

    // 환불 확인 이메일
    void sendRefundConfirmationEmail(String email, Long orderId, String orderNumber);

    void sendRefundRequestEmail(String email, Long orderId, String orderNumber);
    void sendRefundRejectionEmail(String email, Long orderId, String orderNumber, String reason);

    /**
     * 배송 시작 알림 이메일 발송
     *
     * @param email 수신자 이메일
     * @param orderId 주문 ID
     * @param orderNumber 주문 번호
     * @param carrier 배송사
     * @param trackingNumber 운송장 번호
     */
    void sendShipmentNotificationEmail(String email, Long orderId, String orderNumber, String carrier, String trackingNumber);

    /**
     * 배송 완료 알림 이메일 발송
     *
     * @param email 수신자 이메일
     * @param orderId 주문 ID
     * @param orderNumber 주문 번호
     */
    void sendDeliveryCompletionEmail(String email, Long orderId, String orderNumber);

    void sendDeliveryConfirmationEmail(String email, Long orderId, String orderNumber);
    void sendCancellationConfirmationEmail(String email, Long orderId, String orderNumber);
}