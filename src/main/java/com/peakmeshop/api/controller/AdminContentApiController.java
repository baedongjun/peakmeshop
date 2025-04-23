package com.peakmeshop.api.controller;

import com.peakmeshop.api.dto.FaqDTO;
import com.peakmeshop.domain.entity.Faq;
import com.peakmeshop.domain.entity.Inquiry;
import com.peakmeshop.domain.service.FaqService;
import com.peakmeshop.domain.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/contents")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminContentApiController {

    private final FaqService faqService;
    private final InquiryService inquiryService;

    // FAQ 관련 API
    @GetMapping("/faqs")
    public ResponseEntity<Page<FaqDTO>> getFaqs(
            @RequestParam(required = false) String category,
            Pageable pageable) {
        return ResponseEntity.ok(faqService.getFaqListByCategory(category, pageable));
    }

    @GetMapping("/faqs/{id}")
    public ResponseEntity<FaqDTO> getFaq(@PathVariable Long id) {
        return ResponseEntity.ok(faqService.getFaqById(id));
    }

    @PostMapping("/faqs")
    public ResponseEntity<FaqDTO> createFaq(@RequestBody Faq faq) {
        return ResponseEntity.ok(faqService.createFaq(faq));
    }

    @PutMapping("/faqs/{id}")
    public ResponseEntity<FaqDTO> updateFaq(@PathVariable Long id, @RequestBody Faq faq) {
        return ResponseEntity.ok(faqService.updateFaq(id, faq));
    }

    @DeleteMapping("/faqs/{id}")
    public ResponseEntity<Void> deleteFaq(@PathVariable Long id) {
        faqService.deleteFaq(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/faqs/{id}/order")
    public ResponseEntity<Void> changeFaqOrder(
            @PathVariable Long id,
            @RequestParam int newOrder) {
        faqService.changeFaqOrder(id, newOrder);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/faqs/{id}/toggle")
    public ResponseEntity<Void> toggleFaqStatus(@PathVariable Long id) {
        faqService.toggleFaqStatus(id);
        return ResponseEntity.ok().build();
    }

    // 문의 관련 API
    @GetMapping("/inquiries")
    public ResponseEntity<Page<Inquiry>> getInquiries(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Inquiry.InquiryStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(inquiryService.getInquiries(category, status, pageable));
    }

    @GetMapping("/inquiries/{id}")
    public ResponseEntity<Inquiry> getInquiry(@PathVariable Long id) {
        return ResponseEntity.ok(inquiryService.getInquiry(id));
    }

    @PutMapping("/inquiries/{id}/answer")
    public ResponseEntity<Inquiry> answerInquiry(
            @PathVariable Long id,
            @RequestBody String answer) {
        return ResponseEntity.ok(inquiryService.answerInquiry(id, answer));
    }

    @PutMapping("/inquiries/{id}/status")
    public ResponseEntity<Inquiry> changeInquiryStatus(
            @PathVariable Long id,
            @RequestParam Inquiry.InquiryStatus status) {
        return ResponseEntity.ok(inquiryService.changeInquiryStatus(id, status));
    }

    @DeleteMapping("/inquiries/{id}")
    public ResponseEntity<Void> deleteInquiry(@PathVariable Long id) {
        inquiryService.deleteInquiry(id);
        return ResponseEntity.ok().build();
    }
} 