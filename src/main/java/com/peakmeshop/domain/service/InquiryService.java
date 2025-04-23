package com.peakmeshop.domain.service;

import com.peakmeshop.domain.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InquiryService {
    
    // 문의 목록 조회
    Page<Inquiry> getInquiryList(Pageable pageable);
    
    // 카테고리별 문의 목록 조회
    Page<Inquiry> getInquiryListByCategory(String category, Pageable pageable);
    
    // 상태별 문의 목록 조회
    Page<Inquiry> getInquiryListByStatus(Inquiry.InquiryStatus status, Pageable pageable);
    
    // 문의 상세 조회
    Inquiry getInquiryById(Long id);
    
    // 문의 답변 등록/수정
    Inquiry answerInquiry(Long id, String answer);
    
    // 문의 상태 변경
    Inquiry updateInquiryStatus(Long id, Inquiry.InquiryStatus status);
    
    // 문의 삭제
    void deleteInquiry(Long id);

    // 문의 목록 조회 (컨트롤러용)
    Page<Inquiry> getInquiries(String category, Inquiry.InquiryStatus status, Pageable pageable);

    // 문의 상세 조회 (컨트롤러용)
    Inquiry getInquiry(Long id);

    // 문의 상태 변경 (컨트롤러용)
    Inquiry changeInquiryStatus(Long id, Inquiry.InquiryStatus status);
} 