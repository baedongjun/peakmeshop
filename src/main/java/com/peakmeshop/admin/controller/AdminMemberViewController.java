package com.peakmeshop.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 관리자 회원 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin")
public class AdminMemberViewController {

    /**
     * 회원 관리 페이지
     */
    @GetMapping("/members")
    public String members() {
        return "admin/members";
    }

    @GetMapping("/members/create")
    public String createMemberForm() {
        return "admin/member-form";
    }

    @GetMapping("/members/{id}/edit")
    public String editMemberForm(@PathVariable Long id) {
        return "admin/member-form";
    }

    @GetMapping("/members/{id}")
    public String memberDetail(@PathVariable Long id) {
        return "admin/member-detail";
    }

    /**
     * 포인트 관리 페이지
     */
    @GetMapping("/points")
    public String points() {
        return "admin/points";
    }
}

