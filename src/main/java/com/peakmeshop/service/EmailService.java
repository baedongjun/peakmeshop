package com.peakmeshop.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendOrderConfirmation(String to, String name, String orderNumber) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("orderNumber", orderNumber);

        String htmlContent = templateEngine.process("order-confirmation", context);

        helper.setTo(to);
        helper.setSubject("PeakMeShop - 주문 확인");
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    @Async
    public void sendPasswordResetLink(String to, String name, String resetLink) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("resetLink", resetLink);

        String htmlContent = templateEngine.process("password-reset", context);

        helper.setTo(to);
        helper.setSubject("PeakMeShop - 비밀번호 재설정");
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}