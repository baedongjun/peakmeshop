package com.peakmeshop.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 관리자 회원 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin")
public class MemberViewController {

    /**
     * 회원 관리 페이지
     */
    @GetMapping("/members")
    public String members() {
        return "admin/members";
    }

    /**
     * 포인트 관리 페이지
     */
    @GetMapping("/points")
    public String points() {
        return "admin/points";
    }
}

