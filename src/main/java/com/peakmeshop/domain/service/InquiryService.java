package com.peakmeshop.domain.service;

import com.peakmeshop.domain.entity.Inquiry;
import com.peakmeshop.api.dto.InquiryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InquiryService {
    
    // 문의 목록 조회
    Page<InquiryDTO> getInquiryList(Pageable pageable);
    
    // 카테고리별 문의 목록 조회
    Page<InquiryDTO> getInquiryListByCategory(String category, Pageable pageable);
    
    // 상태별 문의 목록 조회
    Page<InquiryDTO> getInquiryListByStatus(Inquiry.InquiryStatus status, Pageable pageable);
    
    // 문의 상세 조회
    InquiryDTO getInquiryById(Long id);
    
    // 문의 답변 등록/수정
    InquiryDTO answerInquiry(Long id, String answer);
    
    // 문의 상태 변경
    InquiryDTO updateInquiryStatus(Long id, Inquiry.InquiryStatus status);
    
    // 문의 삭제
    void deleteInquiry(Long id);

    // 문의 목록 조회 (컨트롤러용)
    Page<InquiryDTO> getInquiries(String category, Inquiry.InquiryStatus status, Pageable pageable);

    // 문의 상세 조회 (컨트롤러용)
    InquiryDTO getInquiry(Long id);

    // 문의 상태 변경 (컨트롤러용)
    InquiryDTO changeInquiryStatus(Long id, Inquiry.InquiryStatus status);
} 