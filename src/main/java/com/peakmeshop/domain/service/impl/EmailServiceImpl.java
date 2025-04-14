package com.peakmeshop.domain.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.EmailDTO;
import com.peakmeshop.api.dto.EmailSendDTO;
import com.peakmeshop.api.dto.EmailTemplateDTO;
import com.peakmeshop.domain.service.EmailService;
import com.peakmeshop.domain.repository.EmailRepository;
import com.peakmeshop.domain.repository.EmailTemplateRepository;
import com.peakmeshop.domain.entity.Email;
import com.peakmeshop.domain.entity.EmailTemplate;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final EmailRepository emailRepository;
    private final EmailTemplateRepository emailTemplateRepository;

    private static final String SENDER_NAME = "PeakMeShop";
    private static final String SENDER_EMAIL = "noreply@peakmeshop.com";

    @Async
    @Override
    public void sendEmail(EmailSendDTO emailSendDTO) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(SENDER_EMAIL, SENDER_NAME);
            helper.setTo(emailSendDTO.to());
            if (emailSendDTO.cc() != null && emailSendDTO.cc().length > 0) {
                helper.setCc(emailSendDTO.cc());
            }
            if (emailSendDTO.bcc() != null && emailSendDTO.bcc().length > 0) {
                helper.setBcc(emailSendDTO.bcc());
            }
            helper.setSubject(emailSendDTO.subject());
            helper.setText(emailSendDTO.content(), emailSendDTO.isHtml());

            if (emailSendDTO.attachments() != null) {
                for (Map.Entry<String, byte[]> entry : emailSendDTO.attachments().entrySet()) {
                    helper.addAttachment(entry.getKey(), new ByteArrayDataSource(entry.getValue(), "application/octet-stream"));
                }
            }

            mailSender.send(message);
            log.info("이메일 전송 완료: {}", emailSendDTO.to());
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("이메일 전송 실패: {}", e.getMessage());
        }
    }

    @Async
    @Override
    public void sendEmail(String to, String subject, String content) {
        EmailSendDTO emailSendDTO = new EmailSendDTO(
            to,
            null,
            null,
            subject,
            content,
            true,
            null
        );
        sendEmail(emailSendDTO);
    }

    @Async
    @Override
    public void sendSimpleEmail(String to, String subject, String content) {
        sendEmail(to, subject, content);
    }

    @Async
    @Override
    public void sendTemplatedEmail(String to, String subject, String templateName, Map<String, Object> templateModel) {
        Context context = new Context();
        templateModel.forEach(context::setVariable);

        String content = templateEngine.process(templateName, context);

        EmailSendDTO emailSendDTO = new EmailSendDTO(
            to,
            null,
            null,
            subject,
            content,
            true,
            null
        );

        sendEmail(emailSendDTO);
    }

    @Async
    @Override
    public void sendSignupConfirmationEmail(String to, String username) {
        Context context = new Context();
        context.setVariable("username", username);

        String subject = "PeakMeShop 회원가입을 환영합니다!";
        String content = templateEngine.process("email/signup-confirmation", context);

        EmailSendDTO emailSendDTO = new EmailSendDTO(
            to,
            null,
            null,
            subject,
            content,
            true,
            null
        );

        sendEmail(emailSendDTO);
    }

    @Async
    @Override
    public void sendVerificationEmail(String to, String username, String verificationToken) {
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("verificationToken", verificationToken);
        context.setVariable("verificationUrl", "http://localhost/api/auth/verify-email?token=" + verificationToken);

        String subject = "PeakMeShop 이메일 인증";
        String content = templateEngine.process("email/email-verification", context);

        EmailSendDTO emailSendDTO = new EmailSendDTO(
            to,
            null,
            null,
            subject,
            content,
            true,
            null
        );

        sendEmail(emailSendDTO);
    }

    @Async
    @Override
    public void sendVerificationEmail(String to, String verificationToken) {
        Context context = new Context();
        context.setVariable("verificationToken", verificationToken);
        context.setVariable("verificationUrl", "https://peakmeshop.com/verify-email?token=" + verificationToken);

        String subject = "PeakMeShop 이메일 인증";
        String content = templateEngine.process("email/email-verification-simple", context);

        EmailSendDTO emailSendDTO = new EmailSendDTO(
            to,
            null,
            null,
            subject,
            content,
            true,
            null
        );

        sendEmail(emailSendDTO);
    }

    @Async
    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        Context context = new Context();
        context.setVariable("resetToken", resetToken);
        context.setVariable("resetUrl", "https://peakmeshop.com/reset-password?token=" + resetToken);

        String subject = "PeakMeShop 비밀번호 재설정";
        String content = templateEngine.process("email/password-reset", context);

        EmailSendDTO emailSendDTO = new EmailSendDTO(
            to,
            null,
            null,
            subject,
            content,
            true,
            null
        );

        sendEmail(emailSendDTO);
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

        EmailSendDTO emailSendDTO = new EmailSendDTO(
            to,
            null,
            null,
            subject,
            content,
            true,
            null
        );

        sendEmail(emailSendDTO);
    }

    @Async
    @Override
    public void sendPasswordResetRequestEmail(String to, String resetToken) {
        Context context = new Context();
        context.setVariable("resetToken", resetToken);
        context.setVariable("resetUrl", "https://peakmeshop.com/reset-password?token=" + resetToken);

        String subject = "PeakMeShop 비밀번호 재설정 요청";
        String content = templateEngine.process("email/password-reset-request-simple", context);

        EmailSendDTO emailSendDTO = new EmailSendDTO(
            to,
            null,
            null,
            subject,
            content,
            true,
            null
        );

        sendEmail(emailSendDTO);
    }

    @Async
    @Override
    public void sendOrderConfirmationEmail(String email, Long orderId, String orderNumber) {
        String subject = "주문이 확인되었습니다.";
        String template = "order-confirmation";
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", orderId);
        variables.put("orderNumber", orderNumber);
        
        sendTemplatedEmail(email, subject, template, variables);
    }

    @Async
    @Override
    public void sendShippingNotificationEmail(String email, Long orderId, String orderNumber, String carrier, String trackingNumber) {
        String subject = "상품이 발송되었습니다.";
        String template = "shipping-notification";
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", orderId);
        variables.put("orderNumber", orderNumber);
        variables.put("carrier", carrier);
        variables.put("trackingNumber", trackingNumber);
        
        sendTemplatedEmail(email, subject, template, variables);
    }

    @Async
    @Override
    public void sendCouponEmail(String to, String couponCode, String couponName) {
        Context context = new Context();
        context.setVariable("couponCode", couponCode);
        context.setVariable("couponName", couponName);

        String subject = "PeakMeShop 쿠폰이 발급되었습니다!";
        String content = templateEngine.process("email/coupon", context);

        EmailSendDTO emailSendDTO = new EmailSendDTO(
            to,
            null,
            null,
            subject,
            content,
            true,
            null
        );

        sendEmail(emailSendDTO);
    }

    @Override
    public void sendDeliveryConfirmationEmail(String email, Long orderId, String orderNumber) {
        Context context = new Context();
        context.setVariable("orderId", orderId);
        context.setVariable("orderNumber", orderNumber);

        String subject = "배송이 완료되었습니다.";
        String content = templateEngine.process("email/delivery-confirmation", context);

        EmailSendDTO emailSendDTO = new EmailSendDTO(
            email,
            null,
            null,
            subject,
            content,
            true,
            null
        );

        sendEmail(emailSendDTO);
    }

    @Override
    public void sendShipmentNotificationEmail(String email, Long orderId, String orderNumber, String carrier, String trackingNumber) {
        Context context = new Context();
        context.setVariable("orderId", orderId);
        context.setVariable("orderNumber", orderNumber);
        context.setVariable("carrier", carrier);
        context.setVariable("trackingNumber", trackingNumber);

        String subject = "상품이 발송되었습니다.";
        String content = templateEngine.process("email/shipment-notification", context);

        EmailSendDTO emailSendDTO = new EmailSendDTO(
            email,
            null,
            null,
            subject,
            content,
            true,
            null
        );

        sendEmail(emailSendDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmailDTO> getEmails(String status, Pageable pageable) {
        return emailRepository.findByStatus(status, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmailTemplateDTO> getEmailTemplates(Pageable pageable) {
        return emailTemplateRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    private EmailDTO convertToDTO(Email email) {
        return new EmailDTO(
            email.getId(),
            email.getTemplate().getId(),
            email.getRecipient(),
            email.getSubject(),
            email.getContent(),
            email.getStatus(),
            email.getErrorMessage(),
            email.getCreatedAt(),
            email.getSentAt()
        );
    }

    private EmailTemplateDTO convertToDTO(EmailTemplate template) {
        return new EmailTemplateDTO(
            template.getId(),
            template.getCode(),
            template.getName(),
            template.getSubject(),
            template.getContent(),
            template.isActive(),
            template.getCreatedAt(),
            template.getUpdatedAt()
        );
    }

    @Override
    public void sendCancellationConfirmationEmail(String email, Long orderId, String orderNumber) {
        Context context = new Context();
        context.setVariable("orderId", orderId);
        context.setVariable("orderNumber", orderNumber);

        String subject = "주문이 취소되었습니다.";
        String content = templateEngine.process("email/cancellation-confirmation", context);

        EmailSendDTO emailSendDTO = new EmailSendDTO(
            email,
            null,
            null,
            subject,
            content,
            true,
            null
        );

        sendEmail(emailSendDTO);
    }

    @Override
    public void sendRefundConfirmationEmail(String email, Long orderId, String orderNumber) {
        Context context = new Context();
        context.setVariable("orderId", orderId);
        context.setVariable("orderNumber", orderNumber);

        String subject = "환불이 완료되었습니다.";
        String content = templateEngine.process("email/refund-confirmation", context);

        EmailSendDTO emailSendDTO = new EmailSendDTO(
            email,
            null,
            null,
            subject,
            content,
            true,
            null
        );

        sendEmail(emailSendDTO);
    }

    @Override
    public void sendRefundRequestEmail(String email, Long orderId, String orderNumber) {
        Context context = new Context();
        context.setVariable("orderId", orderId);
        context.setVariable("orderNumber", orderNumber);

        String subject = "환불 요청이 접수되었습니다.";
        String content = templateEngine.process("email/refund-request", context);

        EmailSendDTO emailSendDTO = new EmailSendDTO(
            email,
            null,
            null,
            subject,
            content,
            true,
            null
        );

        sendEmail(emailSendDTO);
    }

    @Override
    public void sendRefundRejectionEmail(String email, Long orderId, String orderNumber, String reason) {
        Context context = new Context();
        context.setVariable("orderId", orderId);
        context.setVariable("orderNumber", orderNumber);
        context.setVariable("reason", reason);

        String subject = "환불 요청이 거부되었습니다.";
        String content = templateEngine.process("email/refund-rejection", context);

        EmailSendDTO emailSendDTO = new EmailSendDTO(
            email,
            null,
            null,
            subject,
            content,
            true,
            null
        );

        sendEmail(emailSendDTO);
    }

    @Override
    public void sendDeliveryCompletionEmail(String email, Long orderId, String orderNumber) {
        Context context = new Context();
        context.setVariable("orderId", orderId);
        context.setVariable("orderNumber", orderNumber);

        String subject = "배송이 완료되었습니다.";
        String content = templateEngine.process("email/delivery-completion", context);

        EmailSendDTO emailSendDTO = new EmailSendDTO(
            email,
            null,
            null,
            subject,
            content,
            true,
            null
        );

        sendEmail(emailSendDTO);
    }
}