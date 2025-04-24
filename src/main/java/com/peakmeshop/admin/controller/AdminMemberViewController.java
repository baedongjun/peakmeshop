package com.peakmeshop.admin.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.peakmeshop.domain.service.MemberService;
import com.peakmeshop.api.dto.MemberDTO;
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
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String searchKeyword,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {
        // 검색 조건 설정
        if (grade != null) {
            model.addAttribute("grade", grade);
        }
        if (status != null) {
            model.addAttribute("status", status);
        }
        if (searchType != null) {
            model.addAttribute("searchType", searchType);
        }
        if (searchKeyword != null) {
            model.addAttribute("searchKeyword", searchKeyword);
        }

        // 회원 목록 조회
        Page<MemberDTO> members;
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            members = memberService.searchMembers(searchKeyword, pageable);
        } else {
            members = memberService.getAllMembers(pageable);
        }

        // 페이지네이션 정보 추가
        model.addAttribute("members", members);
        model.addAttribute("currentPage", members.getNumber());
        model.addAttribute("totalPages", members.getTotalPages());
        model.addAttribute("totalElements", members.getTotalElements());
        model.addAttribute("pageSize", members.getSize());
        model.addAttribute("hasPrevious", members.hasPrevious());
        model.addAttribute("hasNext", members.hasNext());
        model.addAttribute("isFirst", members.isFirst());
        model.addAttribute("isLast", members.isLast());

        // 기타 정보 추가
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
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String searchKeyword,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {
        // 검색 조건 설정
        if (searchType != null) {
            model.addAttribute("searchType", searchType);
        }
        if (searchKeyword != null) {
            model.addAttribute("searchKeyword", searchKeyword);
        }

        // 휴면 회원 목록 조회
        Page<MemberDTO> members;
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            members = memberService.searchMembers(searchKeyword, pageable);
        } else {
            members = memberService.getDormantMembers(pageable);
        }

        // 페이지네이션 정보 추가
        model.addAttribute("members", members);
        model.addAttribute("currentPage", members.getNumber());
        model.addAttribute("totalPages", members.getTotalPages());
        model.addAttribute("totalElements", members.getTotalElements());
        model.addAttribute("pageSize", members.getSize());
        model.addAttribute("hasPrevious", members.hasPrevious());
        model.addAttribute("hasNext", members.hasNext());
        model.addAttribute("isFirst", members.isFirst());
        model.addAttribute("isLast", members.isLast());

        return "admin/members/dormant";
    }

    /**
     * 탈퇴 회원 관리 페이지
     */
    @GetMapping("/members/withdrawn")
    public String withdrawnMembers(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String searchKeyword,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {
        // 검색 조건 설정
        if (startDate != null) {
            model.addAttribute("startDate", startDate);
        }
        if (endDate != null) {
            model.addAttribute("endDate", endDate);
        }
        if (searchType != null) {
            model.addAttribute("searchType", searchType);
        }
        if (searchKeyword != null) {
            model.addAttribute("searchKeyword", searchKeyword);
        }

        // 탈퇴 회원 목록 조회
        Page<MemberDTO> members;
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            members = memberService.searchMembers(searchKeyword, pageable);
        } else {
            members = memberService.getWithdrawnMembers(startDate, endDate, pageable);
        }

        // 페이지네이션 정보 추가
        model.addAttribute("members", members);
        model.addAttribute("currentPage", members.getNumber());
        model.addAttribute("totalPages", members.getTotalPages());
        model.addAttribute("totalElements", members.getTotalElements());
        model.addAttribute("pageSize", members.getSize());
        model.addAttribute("hasPrevious", members.hasPrevious());
        model.addAttribute("hasNext", members.hasNext());
        model.addAttribute("isFirst", members.isFirst());
        model.addAttribute("isLast", members.isLast());

        return "admin/members/withdrawn";
    }
}

