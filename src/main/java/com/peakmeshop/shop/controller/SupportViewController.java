package com.peakmeshop.shop.controller;

import com.peakmeshop.api.dto.FaqDTO;
import com.peakmeshop.api.dto.NoticeDTO;
import com.peakmeshop.domain.service.FaqService;
import com.peakmeshop.domain.service.InquiryService;
import com.peakmeshop.domain.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 고객지원 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/support")
@RequiredArgsConstructor
public class SupportViewController {
    private final NoticeService noticeService;
    private final FaqService faqService;
    private final InquiryService inquiryService;

    /**
     * 고객지원 메인
     */
    @GetMapping
    public String supportMain(Model model) {
        try {
            // 공지사항 최근 5개
            Page<NoticeDTO> notices = noticeService.getAllNotices(
                    PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))
            );
            model.addAttribute("notices", notices.getContent());

            // 자주 묻는 질문 최근 5개
            Page<FaqDTO> faqs = faqService.getFaqList(
                    PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "viewCount"))
            );
            model.addAttribute("faqs", faqs.getContent());

            return "shop/support/index";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "고객지원 페이지를 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 공지사항 목록
     */
    @GetMapping("/notices")
    public String noticeList(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String keyword,
            Model model) {
        try {
            // 공지사항 목록 로드
            Page<NoticeDTO> notices = noticeService.getNotices(keyword, pageable);
            model.addAttribute("notices", notices);
            model.addAttribute("keyword", keyword);

            return "shop/support/notices";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "공지사항 목록을 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 공지사항 상세
     */
    @GetMapping("/notices/{id}")
    public String noticeDetail(@PathVariable Long id, Model model) {
        try {
            // 공지사항 정보 로드
            NoticeDTO notice = noticeService.getNoticeById(id);
            if (notice == null) {
                return "error/404";
            }
            model.addAttribute("notice", notice);

            // 조회수 증가
            noticeService.incrementViewCount(id);

            // 이전글, 다음글
            model.addAttribute("prevNotice", noticeService.getPrevNotice(id));
            model.addAttribute("nextNotice", noticeService.getNextNotice(id));

            return "shop/support/notice-detail";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "공지사항을 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * FAQ 목록
     */
    @GetMapping("/faqs")
    public String faqList(
            @PageableDefault(size = 10, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            Model model) {
        try {
            // FAQ 목록 로드
            Page<FaqDTO> faqs = faqService.getFaqs(category, keyword, pageable);
            model.addAttribute("faqs", faqs);

            // FAQ 카테고리 목록
            model.addAttribute("categories", faqService.getFaqCategories());
            model.addAttribute("selectedCategory", category);
            model.addAttribute("keyword", keyword);

            return "shop/support/faqs";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "FAQ 목록을 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 1:1 문의하기
     */
    @GetMapping("/inquiry")
    public String inquiry(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long productId,
            Model model) {
        try {
            // 문의 유형 로드
            model.addAttribute("inquiryTypes", inquiryService.getInquiryTypes());

            // 상품 정보 로드 (있는 경우)
            if (productId != null) {
                model.addAttribute("product", productService.getProductById(productId));
            }

            return "shop/support/inquiry-form";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "문의 페이지를 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 이용약관
     */
    @GetMapping("/terms")
    public String terms(
            @RequestParam(required = false, defaultValue = "service") String type,
            Model model) {
        try {
            model.addAttribute("type", type);
            switch (type) {
                case "privacy":
                    model.addAttribute("title", "개인정보처리방침");
                    model.addAttribute("content", termsService.getPrivacyPolicy());
                    break;
                case "location":
                    model.addAttribute("title", "위치기반서비스 이용약관");
                    model.addAttribute("content", termsService.getLocationTerms());
                    break;
                default:
                    model.addAttribute("title", "이용약관");
                    model.addAttribute("content", termsService.getServiceTerms());
                    break;
            }
            return "shop/support/terms";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "약관을 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 이벤트 목록
     */
    @GetMapping("/events")
    public String eventList(
            @PageableDefault(size = 12, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String status,
            Model model) {
        try {
            // 이벤트 목록 로드
            Page<EventDTO> events = eventService.getEvents(status, pageable);
            model.addAttribute("events", events);
            model.addAttribute("status", status);

            return "shop/support/events";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "이벤트 목록을 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 이벤트 상세
     */
    @GetMapping("/events/{id}")
    public String eventDetail(@PathVariable Long id, Model model) {
        try {
            // 이벤트 정보 로드
            EventDTO event = eventService.getEventById(id);
            if (event == null) {
                return "error/404";
            }
            model.addAttribute("event", event);

            // 조회수 증가
            eventService.incrementViewCount(id);

            return "shop/support/event-detail";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "이벤트를 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }
}

