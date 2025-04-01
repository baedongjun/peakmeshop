package com.peakmeshop.domain.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.peakmeshop.api.dto.EmailDTO;
import com.peakmeshop.domain.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    private static final String SENDER_NAME = "PeakMeShop";
    private static final String SENDER_EMAIL = "noreply@peakmeshop.com";

    @Async
    @Override
    public void sendEmail(EmailDTO emailDTO) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(SENDER_EMAIL, SENDER_NAME);
            helper.setTo(emailDTO.getTo());
            helper.setSubject(emailDTO.getSubject());
            helper.setText(emailDTO.getContent(), emailDTO.isHtml());

            mailSender.send(message);
            log.info("이메일 전송 완료: {}", emailDTO.getTo());
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("이메일 전송 실패: {}", e.getMessage());
        }
    }

    @Async
    @Override
    public void sendEmail(String to, String subject, String content) {
        EmailDTO emailDTO = EmailDTO.builder()
                .to(to)
                .subject(subject)
                .content(content)
                .isHtml(true)
                .build();

        sendEmail(emailDTO);
    }

    @Async
    @Override
    public void sendSimpleEmail(String to, String subject, String content) {
        // sendEmail 메서드를 재사용
        sendEmail(to, subject, content);
    }

    @Async
    @Override
    public void sendTemplatedEmail(String to, String subject, String templateName, Map<String, Object> templateModel) {
        Context context = new Context();
        templateModel.forEach(context::setVariable);

        String content = templateEngine.process(templateName, context);

        EmailDTO emailDTO = EmailDTO.builder()
                .to(to)
                .subject(subject)
                .content(content)
                .isHtml(true)
                .build();

        sendEmail(emailDTO);
    }

    @Async
    @Override
    public void sendSignupConfirmationEmail(String to, String username) {
        Context context = new Context();
        context.setVariable("username", username);

        String subject = "PeakMeShop 회원가입을 환영합니다!";
        String content = templateEngine.process("email/signup-confirmation", context);

        EmailDTO emailDTO = EmailDTO.builder()
                .to(to)
                .subject(subject)
                .content(content)
                .isHtml(true)
                .build();

        sendEmail(emailDTO);
    }

    @Async
    @Override
    public void sendVerificationEmail(String to, String username, String verificationToken) {
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("verificationToken", verificationToken);
        context.setVariable("verificationUrl", "https://localhost/verify-email?token=" + verificationToken);

        String subject = "PeakMeShop 이메일 인증";
        String content = templateEngine.process("email/email-verification", context);

        EmailDTO emailDTO = EmailDTO.builder()
                .to(to)
                .subject(subject)
                .content(content)
                .isHtml(true)
                .build();

        sendEmail(emailDTO);
    }

    @Async
    @Override
    public void sendVerificationEmail(String to, String verificationToken) {
        // 사용자 이름 없이 이메일 인증 메일 전송
        Context context = new Context();
        context.setVariable("verificationToken", verificationToken);
        context.setVariable("verificationUrl", "https://peakmeshop.com/verify-email?token=" + verificationToken);

        String subject = "PeakMeShop 이메일 인증";
        String content = templateEngine.process("email/email-verification-simple", context);

        EmailDTO emailDTO = EmailDTO.builder()
                .to(to)
                .subject(subject)
                .content(content)
                .isHtml(true)
                .build();

        sendEmail(emailDTO);
    }

    @Async
    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        Context context = new Context();
        context.setVariable("resetToken", resetToken);
        context.setVariable("resetUrl", "https://peakmeshop.com/reset-password?token=" + resetToken);

        String subject = "PeakMeShop 비밀번호 재설정";
        String content = templateEngine.process("email/password-reset", context);

        EmailDTO emailDTO = EmailDTO.builder()
                .to(to)
                .subject(subject)
                .content(content)
                .isHtml(true)
                .build();

        sendEmail(emailDTO);
    }

    @Async
    @Override
    public void sendPasswordResetRequestEmail(String to, String username, String resetToken) {
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("resetToken", resetToken);
        context.setVariable("resetUrl", "https://peakmeshop.com/reset-password?token=" + resetToken);

        String subject = "PeakMeShop 비밀번호 재설정 요청";
        String content = templateEngine.process("email/password-reset-request", context);

        EmailDTO emailDTO = EmailDTO.builder()
                .to(to)
                .subject(subject)
                .content(content)
                .isHtml(true)
                .build();

        sendEmail(emailDTO);
    }

    @Async
    @Override
    public void sendPasswordResetRequestEmail(String to, String resetToken) {
        // 사용자 이름 없이 비밀번호 재설정 요청 메일 전송
        Context context = new Context();
        context.setVariable("resetToken", resetToken);
        context.setVariable("resetUrl", "https://peakmeshop.com/reset-password?token=" + resetToken);

        String subject = "PeakMeShop 비밀번호 재설정 요청";
        String content = templateEngine.process("email/password-reset-request-simple", context);

        EmailDTO emailDTO = EmailDTO.builder()
                .to(to)
                .subject(subject)
                .content(content)
                .isHtml(true)
                .build();

        sendEmail(emailDTO);
    }

    @Async
    @Override
    public void sendOrderConfirmationEmail(String to, Long orderId, String orderNumber) {
        Context context = new Context();
        context.setVariable("orderId", orderId);
        context.setVariable("orderNumber", orderNumber);
        context.setVariable("orderUrl", "https://peakmeshop.com/orders/" + orderId);

        String subject = "PeakMeShop 주문 확인 [주문번호: " + orderNumber + "]";
        String content = templateEngine.process("email/order-confirmation", context);

        EmailDTO emailDTO = EmailDTO.builder()
                .to(to)
                .subject(subject)
                .content(content)
                .isHtml(true)
                .build();

        sendEmail(emailDTO);
    }

    @Async
    @Override
    public void sendShippingNotificationEmail(String to, Long orderId, String trackingNumber) {
        Context context = new Context();
        context.setVariable("orderId", orderId);
        context.setVariable("trackingNumber", trackingNumber);
        context.setVariable("orderUrl", "https://peakmeshop.com/orders/" + orderId);
        context.setVariable("trackingUrl", "https://peakmeshop.com/tracking/" + trackingNumber);

        String subject = "PeakMeShop 상품 배송 시작";
        String content = templateEngine.process("email/shipping-notification", context);

        EmailDTO emailDTO = EmailDTO.builder()
                .to(to)
                .subject(subject)
                .content(content)
                .isHtml(true)
                .build();

        sendEmail(emailDTO);
    }

    @Async
    @Override
    public void sendCouponEmail(String to, String couponCode, String couponName) {
        Context context = new Context();
        context.setVariable("couponCode", couponCode);
        context.setVariable("couponName", couponName);

        String subject = "PeakMeShop 쿠폰이 발급되었습니다!";
        String content = templateEngine.process("email/coupon", context);

        EmailDTO emailDTO = EmailDTO.builder()
                .to(to)
                .subject(subject)
                .content(content)
                .isHtml(true)
                .build();

        sendEmail(emailDTO);
    }

    @Async
    @Override
    public void sendRefundConfirmationEmail(String to, Long orderId, String orderNumber) {
        Context context = new Context();
        context.setVariable("orderId", orderId);
        context.setVariable("orderNumber", orderNumber);
        context.setVariable("orderUrl", "https://peakmeshop.com/orders/" + orderId);

        String subject = "PeakMeShop 환불 처리 완료 [주문번호: " + orderNumber + "]";
        String content = templateEngine.process("email/refund-confirmation", context);

        EmailDTO emailDTO = EmailDTO.builder()
                .to(to)
                .subject(subject)
                .content(content)
                .isHtml(true)
                .build();

        sendEmail(emailDTO);
    }

    @Override
    public void sendRefundRequestEmail(String email, Long orderId, String orderNumber) {
        try {
            Context context = new Context();
            context.setVariable("orderNumber", orderNumber);
            context.setVariable("orderId", orderId);

            String subject = "환불 요청이 접수되었습니다";
            String content = templateEngine.process("emails/refund-request", context);

            sendEmail(email, subject, content);
            log.info("환불 요청 이메일 발송 완료: 이메일={}, 주문번호={}", email, orderNumber);
        } catch (Exception e) {
            log.error("환불 요청 이메일 발송 실패: {}", e.getMessage());
        }
    }

    @Override
    public void sendRefundRejectionEmail(String email, Long orderId, String orderNumber, String reason) {
        try {
            Context context = new Context();
            context.setVariable("orderNumber", orderNumber);
            context.setVariable("orderId", orderId);
            context.setVariable("reason", reason);

            String subject = "환불 요청이 거부되었습니다";
            String content = templateEngine.process("emails/refund-rejection", context);

            sendEmail(email, subject, content);
            log.info("환불 거부 이메일 발송 완료: 이메일={}, 주문번호={}", email, orderNumber);
        } catch (Exception e) {
            log.error("환불 거부 이메일 발송 실패: {}", e.getMessage());
        }
    }

    @Override
    public void sendShipmentNotificationEmail(String email, Long orderId, String orderNumber, String carrier, String trackingNumber) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("[PeakMeShop] 주문하신 상품이 발송되었습니다. (주문번호: " + orderNumber + ")");

            Context context = new Context();
            Map<String, Object> variables = new HashMap<>();
            variables.put("orderId", orderId);
            variables.put("orderNumber", orderNumber);
            variables.put("carrier", carrier);
            variables.put("trackingNumber", trackingNumber);

            // 배송사별 배송조회 URL 설정
            String trackingUrl = getTrackingUrl(carrier, trackingNumber);
            variables.put("trackingUrl", trackingUrl);

            context.setVariables(variables);

            String htmlContent = templateEngine.process("email/shipment-notification", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("배송 시작 알림 이메일 발송 완료: 수신자={}, 주문번호={}", email, orderNumber);
        } catch (MessagingException e) {
            log.error("배송 시작 알림 이메일 발송 실패: {}", e.getMessage());
            throw new RuntimeException("이메일 발송 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public void sendDeliveryCompletionEmail(String email, Long orderId, String orderNumber) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("[PeakMeShop] 주문하신 상품이 배송 완료되었습니다. (주문번호: " + orderNumber + ")");

            Context context = new Context();
            Map<String, Object> variables = new HashMap<>();
            variables.put("orderId", orderId);
            variables.put("orderNumber", orderNumber);

            context.setVariables(variables);

            String htmlContent = templateEngine.process("email/delivery-completion", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("배송 완료 알림 이메일 발송 완료: 수신자={}, 주문번호={}", email, orderNumber);
        } catch (MessagingException e) {
            log.error("배송 완료 알림 이메일 발송 실패: {}", e.getMessage());
            throw new RuntimeException("이메일 발송 중 오류가 발생했습니다.", e);
        }
    }

    // 배송사별 배송조회 URL 반환 메서드
    private String getTrackingUrl(String carrier, String trackingNumber) {
        switch (carrier.toUpperCase()) {
            case "CJ대한통운":
                return "https://www.cjlogistics.com/ko/tool/parcel/tracking?gnbInvcNo=" + trackingNumber;
            case "우체국택배":
                return "https://service.epost.go.kr/trace.RetrieveDomRigiTraceList.comm?sid1=" + trackingNumber;
            case "한진택배":
                return "https://www.hanjin.co.kr/kor/CMS/DeliveryMgr/WaybillResult.do?mCode=MN038&schLang=KR&wblnumText2=" + trackingNumber;
            case "롯데택배":
                return "https://www.lotteglogis.com/home/reservation/tracking/index?InvNo=" + trackingNumber;
            case "로젠택배":
                return "https://www.ilogen.com/m/personal/trace/" + trackingNumber;
            default:
                return "#";
        }
    }
}