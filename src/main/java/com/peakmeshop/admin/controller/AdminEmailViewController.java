package com.peakmeshop.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 관리자 이메일 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin")
public class AdminEmailViewController {

    /**
     * 이메일 발송 관리 페이지
     */
    @GetMapping("/emails")
    public String emails() {
        return "admin/emails";
    }

    /**
     * 이메일 템플릿 관리 페이지
     */
    @GetMapping("/email-templates")
    public String emailTemplates() {
        return "admin/email-templates";
    }

    /**
     * 이메일 템플릿 등록 페이지
     */
    @GetMapping("/email-templates/new")
    public String newEmailTemplate() {
        return "admin/email-template-form";
    }

    /**
     * 이메일 템플릿 수정 페이지
     */
    @GetMapping("/email-templates/edit/{id}")
    public String editEmailTemplate(@PathVariable Long id, Model model) {
        return "admin/email-template-form";
    }
}

