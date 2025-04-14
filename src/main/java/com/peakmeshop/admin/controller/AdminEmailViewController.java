package com.peakmeshop.admin.controller;

import com.peakmeshop.api.dto.EmailDTO;
import com.peakmeshop.api.dto.EmailTemplateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.peakmeshop.domain.service.EmailService;
import lombok.RequiredArgsConstructor;

/**
 * 관리자 이메일 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminEmailViewController {

    private final EmailService emailService;

    /**
     * 이메일 발송 관리 페이지
     */
    @GetMapping("/emails")
    public String emails(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {
        if (status != null) {
            model.addAttribute("status", status);
        }
        
        Page<EmailDTO> emails = emailService.getEmails(status, pageable);
        model.addAttribute("emails", emails);
        model.addAttribute("currentPage", emails.getNumber());
        model.addAttribute("totalPages", emails.getTotalPages());
        model.addAttribute("totalElements", emails.getTotalElements());
        model.addAttribute("pageSize", emails.getSize());
        model.addAttribute("hasPrevious", emails.hasPrevious());
        model.addAttribute("hasNext", emails.hasNext());
        model.addAttribute("isFirst", emails.isFirst());
        model.addAttribute("isLast", emails.isLast());
        
        return "admin/emails/emails";
    }

    /**
     * 이메일 발송 페이지
     */
    @GetMapping("/emails/send")
    public String sendEmail(
            @RequestParam(required = false) String type,
            Model model) {
        if (type != null) {
            model.addAttribute("type", type);
        }
        return "admin/emails/send";
    }

    /**
     * 이메일 발송 내역 상세 페이지
     */
    @GetMapping("/emails/{id}")
    public String emailDetail(@PathVariable Long id, Model model) {
        model.addAttribute("emailId", id);
        return "admin/emails/detail";
    }

    /**
     * 이메일 템플릿 관리 페이지
     */
    @GetMapping("/email-templates")
    public String emailTemplates(
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {
        Page<EmailTemplateDTO> templates = emailService.getEmailTemplates(pageable);
        model.addAttribute("templates", templates);
        model.addAttribute("currentPage", templates.getNumber());
        model.addAttribute("totalPages", templates.getTotalPages());
        model.addAttribute("totalElements", templates.getTotalElements());
        model.addAttribute("pageSize", templates.getSize());
        model.addAttribute("hasPrevious", templates.hasPrevious());
        model.addAttribute("hasNext", templates.hasNext());
        model.addAttribute("isFirst", templates.isFirst());
        model.addAttribute("isLast", templates.isLast());
        
        return "admin/emails/templates";
    }

    /**
     * 이메일 템플릿 등록 페이지
     */
    @GetMapping("/email-templates/new")
    public String createEmailTemplate() {
        return "admin/emails/template-form";
    }

    /**
     * 이메일 템플릿 수정 페이지
     */
    @GetMapping("/email-templates/{id}/edit")
    public String editEmailTemplate(@PathVariable Long id, Model model) {
        model.addAttribute("templateId", id);
        return "admin/emails/template-form";
    }

    /**
     * 이메일 설정 페이지
     */
    @GetMapping("/email-settings")
    public String emailSettings(Model model) {
        return "admin/emails/settings";
    }

    /**
     * 이메일 수신거부 관리 페이지
     */
    @GetMapping("/email-unsubscribes")
    public String emailUnsubscribes(Model model) {
        return "admin/emails/unsubscribes";
    }
}

