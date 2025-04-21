package com.peakmeshop.domain.service;

import com.peakmeshop.api.dto.InquiryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InquiryService {
    
    Page<InquiryDTO> getInquiriesByUserId(String userId, String status, Pageable pageable);
    
    InquiryDTO getInquiryById(Long id);
    
    List<String> getInquiryTypes();
    
    Page<InquiryDTO> getInquiryList(Pageable pageable);
    
    Page<InquiryDTO> getInquiryListByCategory(String category, Pageable pageable);
    
    Page<InquiryDTO> getInquiryListByStatus(String status, Pageable pageable);
    
    InquiryDTO answerInquiry(Long id, String answer);
    
    InquiryDTO updateInquiryStatus(Long id, String status);
    
    void deleteInquiry(Long id);
    
    Page<InquiryDTO> getInquiries(String category, String status, Pageable pageable);
    
    InquiryDTO createInquiry(String userId, InquiryDTO inquiryDTO);
    
    InquiryDTO updateInquiry(String userId, Long inquiryId, InquiryDTO inquiryDTO);
} 