package com.peakmeshop.api.controller;

import java.util.Map;

import com.peakmeshop.api.dto.EmailDTO;
import com.peakmeshop.domain.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/simple")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> sendSimpleEmail(@RequestBody Map<String, String> request) {
        String to = request.get("to");
        String subject = request.get("subject");
        String text = request.get("text");

        emailService.sendSimpleEmail(to, subject, text);

        return ResponseEntity.ok("이메일이 성공적으로 전송되었습니다.");
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> sendEmail(@RequestBody EmailDTO emailDTO) {
        emailService.sendEmail(emailDTO);

        return ResponseEntity.ok("이메일이 성공적으로 전송되었습니다.");
    }

    @PostMapping("/template")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> sendTemplatedEmail(@RequestBody Map<String, Object> request) {
        String to = (String) request.get("to");
        String subject = (String) request.get("subject");
        String templateName = (String) request.get("templateName");

        @SuppressWarnings("unchecked")
        Map<String, Object> variables = (Map<String, Object>) request.get("variables");

        emailService.sendTemplatedEmail(to, subject, templateName, variables);

        return ResponseEntity.ok("이메일이 성공적으로 전송되었습니다.");
    }
}