package com.peakmeshop.admin.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.peakmeshop.domain.service.MemberService;
import lombok.RequiredArgsConstructor;

/**
 * 관리자 회원 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminMemberViewController {

    private final MemberService memberService;

    /**
     * 회원 관리 페이지
     */
    @GetMapping("/members")
    public String members(
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {
        if (grade != null) {
            model.addAttribute("grade", grade);
        }
        if (status != null) {
            model.addAttribute("status", status);
        }

        model.addAttribute("summary", memberService.getMemberSummary());
        model.addAttribute("members", memberService.getAllMembers(pageable));
        model.addAttribute("grades", memberService.getAllGrades());
        
        return "admin/members/members";
    }

    /**
     * 회원 등록 페이지
     */
    @GetMapping("/members/new")
    public String createMember(Model model) {
        model.addAttribute("grades", memberService.getAllGrades());
        return "admin/members/member-form";
    }

    /**
     * 회원 수정 페이지
     */
    @GetMapping("/members/{id}/edit")
    public String editMember(@PathVariable Long id, Model model) {
        model.addAttribute("memberId", id);
        model.addAttribute("member", memberService.getMemberById(id));
        model.addAttribute("grades", memberService.getAllGrades());
        return "admin/members/member-form";
    }

    /**
     * 회원 상세 페이지
     */
    @GetMapping("/members/{id}")
    public String memberDetail(@PathVariable Long id, Model model) {
        model.addAttribute("memberId", id);
        model.addAttribute("member", memberService.getMemberById(id));
        model.addAttribute("summary", memberService.getMemberSummary(id));
        return "member-detail_old";
    }

    /**
     * 회원 등급 관리 페이지
     */
    @GetMapping("/member-grades")
    public String memberGrades(Model model) {
        model.addAttribute("grades", memberService.getAllGrades());
        return "admin/members/grades";
    }

    /**
     * 회원 등급 수정 페이지
     */
    @GetMapping("/member-grades/{id}/edit")
    public String editMemberGrade(@PathVariable Long id, Model model) {
        model.addAttribute("gradeId", id);
        model.addAttribute("grade", memberService.getGradeById(id));
        return "admin/members/grade-form";
    }

    /**
     * 포인트 관리 페이지
     */
    @GetMapping("/points")
    public String points(
            @RequestParam(required = false) String type,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {
        if (type != null) {
            model.addAttribute("type", type);
        }
        model.addAttribute("points", memberService.getPoints(pageable));
        return "admin/members/points";
    }

    /**
     * 포인트 상세 페이지
     */
    @GetMapping("/points/{id}")
    public String pointDetail(@PathVariable Long id, Model model) {
        model.addAttribute("pointId", id);
        model.addAttribute("point", memberService.getPointById(id));
        return "admin/members/point-detail";
    }

    /**
     * 회원 통계 페이지
     */
    @GetMapping("/members/statistics")
    public String memberStatistics(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {
        model.addAttribute("period", period);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("statistics", memberService.getMemberStatistics(period, startDate, endDate));
        return "admin/members/statistics";
    }

    /**
     * 휴면 회원 관리 페이지
     */
    @GetMapping("/members/dormant")
    public String dormantMembers(
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {
        model.addAttribute("members", memberService.getDormantMembers(pageable));
        return "admin/members/dormant";
    }

    /**
     * 탈퇴 회원 관리 페이지
     */
    @GetMapping("/members/withdrawn")
    public String withdrawnMembers(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("members", memberService.getWithdrawnMembers(startDate, endDate, pageable));
        return "admin/members/withdrawn";
    }
}

