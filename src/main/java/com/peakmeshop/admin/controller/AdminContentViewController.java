package com.peakmeshop.admin.controller;

import com.peakmeshop.api.dto.FaqDTO;
import com.peakmeshop.api.dto.NoticeDTO;
import com.peakmeshop.domain.entity.Faq;
import com.peakmeshop.domain.entity.Inquiry;
import com.peakmeshop.domain.service.ContentService;
import com.peakmeshop.domain.service.FaqService;
import com.peakmeshop.domain.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 콘텐츠 관리 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminContentViewController {

    private final ContentService contentService;
    private final FaqService faqService;
    private final InquiryService inquiryService;

    /**
     * 공지사항 관리 페이지
     */
    @GetMapping("/notices")
    public String notices(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {
        if (category != null) {
            model.addAttribute("category", category);
        }
        if (status != null) {
            model.addAttribute("status", status);
        }
        
        Page<NoticeDTO> notices = contentService.getNotices(category, status, pageable);
        model.addAttribute("notices", notices);
        model.addAttribute("currentPage", notices.getNumber());
        model.addAttribute("totalPages", notices.getTotalPages());
        model.addAttribute("totalElements", notices.getTotalElements());
        model.addAttribute("pageSize", notices.getSize());
        model.addAttribute("hasPrevious", notices.hasPrevious());
        model.addAttribute("hasNext", notices.hasNext());
        model.addAttribute("isFirst", notices.isFirst());
        model.addAttribute("isLast", notices.isLast());
        
        return "admin/contents/notices";
    }

    /**
     * 공지사항 등록 페이지
     */
    @GetMapping("/notices/new")
    public String createNotice() {
        return "admin/contents/notice-form";
    }

    /**
     * 공지사항 수정 페이지
     */
    @GetMapping("/notices/{id}/edit")
    public String editNotice(@PathVariable Long id, Model model) {
        model.addAttribute("noticeId", id);
        return "admin/contents/notice-form";
    }

    /**
     * FAQ 관리 페이지
     */
    @GetMapping("/faqs")
    public String faqList(
            @RequestParam(required = false) String category,
            @PageableDefault(sort = "sortOrder", direction = Sort.Direction.ASC) Pageable pageable,
            Model model) {
        model.addAttribute("category", category);
        return "admin/contents/faqs";
    }

    /**
     * FAQ 등록 페이지
     */
    @GetMapping("/faqs/new")
    public String createFaq() {
        return "admin/contents/faq-form";
    }

    /**
     * FAQ 수정 페이지
     */
    @GetMapping("/faqs/{id}/edit")
    public String editFaq(@PathVariable Long id, Model model) {
        model.addAttribute("faqId", id);
        return "admin/contents/faq-form";
    }

    /**
     * FAQ 카테고리 관리 페이지
     */
    @GetMapping("/faqs/categories")
    public String faqCategories(Model model) {
        return "admin/contents/faq-categories";
    }

    /**
     * 1:1 문의 답변 페이지
     */
    @GetMapping("/inquiries/{id}/reply")
    public String replyInquiry(@PathVariable Long id, Model model) {
        model.addAttribute("inquiryId", id);
        return "admin/contents/inquiry-reply";
    }

    /**
     * 약관 관리 페이지
     */
    @GetMapping("/terms")
    public String terms(Model model) {
        return "admin/contents/terms";
    }

    /**
     * 약관 수정 페이지
     */
    @GetMapping("/terms/{type}/edit")
    public String editTerm(@PathVariable String type, Model model) {
        model.addAttribute("termType", type);
        return "admin/contents/term-form";
    }

    // FAQ 등록/수정 페이지
    @GetMapping("/faqs/form")
    public String faqForm(@RequestParam(required = false) Long id, Model model) {
        model.addAttribute("faqId", id);
        return "admin/contents/faq-form";
    }

    // FAQ 등록/수정 처리
    @PostMapping("/faqs/save")
    public String saveFaq(@ModelAttribute FaqDTO faqDTO) {
        if (faqDTO.id() != null) {
            faqService.updateFaq(faqDTO.id(), faqDTO);
        } else {
            faqService.createFaq(faqDTO);
        }
        return "redirect:/admin/faqs";
    }

    // FAQ 삭제
    @PostMapping("/faqs/delete")
    public String deleteFaq(@RequestParam Long id) {
        faqService.deleteFaq(id);
        return "redirect:/admin/faqs";
    }

    // FAQ 순서 변경
    @PostMapping("/faqs/order")
    @ResponseBody
    public void updateFaqOrder(@RequestParam Long id, @RequestParam int newOrder) {
        faqService.updateFaqOrder(id, newOrder);
    }

    // FAQ 활성화/비활성화
    @PostMapping("/faqs/toggle")
    @ResponseBody
    public void toggleFaqStatus(@RequestParam Long id) {
        faqService.toggleFaqStatus(id);
    }

    // 문의 목록 페이지
    @GetMapping("/inquiries")
    public String inquiryList(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Inquiry.InquiryStatus status,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        model.addAttribute("category", category);
        model.addAttribute("status", status);
        return "admin/contents/inquiries";
    }

    // 문의 상세 페이지
    @GetMapping("/inquiries/{id}")
    public String inquiryDetail(@PathVariable Long id, Model model) {
        model.addAttribute("inquiryId", id);
        return "admin/contents/inquiry-detail";
    }

    // 문의 답변 등록/수정
    @PostMapping("/inquiries/answer")
    public String answerInquiry(@RequestParam Long id, @RequestParam String answer) {
        inquiryService.answerInquiry(id, answer);
        return "redirect:/admin/inquiries/" + id;
    }

    // 문의 상태 변경
    @PostMapping("/inquiries/status")
    public String updateInquiryStatus(@RequestParam Long id, @RequestParam Inquiry.InquiryStatus status) {
        inquiryService.updateInquiryStatus(id, status);
        return "redirect:/admin/inquiries/" + id;
    }

    // 문의 삭제
    @PostMapping("/inquiries/delete")
    public String deleteInquiry(@RequestParam Long id) {
        inquiryService.deleteInquiry(id);
        return "redirect:/admin/inquiries";
    }
}

