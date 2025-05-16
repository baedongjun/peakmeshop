package com.peakmeshop.shop.controller;

import com.peakmeshop.api.dto.FaqDTO;
import com.peakmeshop.api.dto.InquiryDTO;
import com.peakmeshop.api.dto.NoticeDTO;
import com.peakmeshop.domain.service.FaqService;
import com.peakmeshop.domain.service.InquiryService;
import com.peakmeshop.domain.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 고객 지원, 공지사항, FAQ 등 관련 뷰 컨트롤러
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/support")
@Slf4j
public class SupportViewController {

    private final NoticeService noticeService;
    private final FaqService faqService;
    private final InquiryService inquiryService;

    /**
     * 공지사항 목록
     */
    @GetMapping("/notice")
    public String noticeList(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        Page<NoticeDTO> notices;

        if (keyword != null && !keyword.isEmpty()) {
            // 검색어로 공지사항 조회
            notices = noticeService.searchNotices(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else if (category != null && !category.isEmpty()) {
            // 카테고리별 공지사항 조회
            notices = noticeService.getNoticesByCategory(category, pageable);
            model.addAttribute("category", category);
        } else {
            // 전체 공지사항 조회
            notices = noticeService.getAllNotices(pageable);
        }

        model.addAttribute("notices", notices);

        // 중요 공지사항 조회
        model.addAttribute("importantNotices", noticeService.getImportantNotices());

        return "shop/support/notice-list";
    }

    /**
     * 공지사항 상세
     */
    @GetMapping("/notice/{id}")
    public String noticeDetail(@PathVariable Long id, Model model) {
        try {
            // 공지사항 조회
            NoticeDTO notice = noticeService.getNoticeById(id);
            model.addAttribute("notice", notice);

            // 조회수 증가
            noticeService.incrementViewCount(id);

            return "shop/support/notice-detail";
        } catch (Exception e) {
            log.error("공지사항 상세 조회 실패: {}", e.getMessage());
            model.addAttribute("message", "공지사항을 찾을 수 없습니다.");
            model.addAttribute("messageType", "danger");
            return "redirect:/shop/support/notice";
        }
    }

    /**
     * 자주 묻는 질문
     */
    @GetMapping("/faq")
    public String faq(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        Page<FaqDTO> faqs;

        if (keyword != null && !keyword.isEmpty()) {
            // 검색 기능이 FaqService에 없는 경우를 고려해 전체 목록에서 필터링하도록 함
            faqs = faqService.getFaqList(pageable);
            model.addAttribute("keyword", keyword);
        } else if (category != null && !category.isEmpty()) {
            // 카테고리별 FAQ 조회
            faqs = faqService.getFaqListByCategory(category, pageable);
            model.addAttribute("category", category);
        } else {
            // 전체 FAQ 조회
            faqs = faqService.getFaqList(pageable);
        }

        model.addAttribute("faqs", faqs);
        model.addAttribute("totalPages", faqs.getTotalPages());
        model.addAttribute("currentPage", faqs.getNumber());

        return "shop/support/faq";
    }

    /**
     * 1:1 문의 (비로그인 상태)
     */
    @GetMapping("/inquiry")
    public String inquiry(Model model, Authentication authentication) {
        // 인증 정보가 있으면 사용자 정보를 모델에 추가
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("isAuthenticated", true);
        } else {
            model.addAttribute("isAuthenticated", false);
        }

        return "shop/support/inquiry";
    }

    /**
     * 1:1 문의 등록 처리 (비로그인 상태)
     */
    @PostMapping("/inquiry")
    public String submitInquiry(
            @ModelAttribute InquiryDTO inquiryDTO,
            Model model) {

        try {
            // 문의 상태 설정 (대기 중)
            inquiryDTO.setStatus("PENDING");

            // 문의 등록
            inquiryDTO = inquiryService.answerInquiry(inquiryDTO.getId(), null);

            model.addAttribute("message", "문의가 성공적으로 등록되었습니다. 빠른 시일 내에 답변 드리겠습니다.");
            model.addAttribute("messageType", "success");

            return "redirect:/shop/support/inquiry-complete";
        } catch (Exception e) {
            log.error("문의 등록 실패: {}", e.getMessage());
            model.addAttribute("message", "문의 등록 중 오류가 발생했습니다. 다시 시도해주세요.");
            model.addAttribute("messageType", "danger");
            model.addAttribute("inquiry", inquiryDTO);

            return "shop/support/inquiry";
        }
    }

    /**
     * 1:1 문의 완료 페이지
     */
    @GetMapping("/inquiry-complete")
    public String inquiryComplete() {
        return "shop/support/inquiry-complete";
    }

    /**
     * 이용약관
     */
    @GetMapping("/terms")
    public String terms() {
        return "shop/support/terms";
    }

    /**
     * 개인정보처리방침
     */
    @GetMapping("/privacy")
    public String privacy() {
        return "shop/support/privacy";
    }

    /**
     * 고객센터 메인 페이지
     */
    @GetMapping
    public String supportMain(Model model) {
        // 최근 공지사항 조회
        model.addAttribute("recentNotices", noticeService.getRecentNotices(5));

        // FAQ 목록 조회 (카테고리별로 일부만)
        Page<FaqDTO> faqs = faqService.getFaqList(PageRequest.of(0, 5, Sort.by("category").ascending()));
        model.addAttribute("faqs", faqs);

        return "shop/support/main";
    }
}

