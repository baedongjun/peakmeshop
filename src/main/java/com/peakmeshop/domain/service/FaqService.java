package com.peakmeshop.domain.service;

import com.peakmeshop.api.dto.FaqDTO;
import com.peakmeshop.domain.entity.Faq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FaqService {
    
    // FAQ 목록 조회
    Page<FaqDTO> getFaqList(Pageable pageable);
    
    // 카테고리별 FAQ 목록 조회
    Page<FaqDTO> getFaqListByCategory(String category, Pageable pageable);

    // FAQ 상세 조회
    FaqDTO getFaqById(Long id);
    
    // FAQ 생성
    FaqDTO createFaq(Faq faq);
    
    // FAQ 수정
    FaqDTO updateFaq(Long id, Faq faq);
    
    // FAQ 삭제
    void deleteFaq(Long id);
    
    // FAQ 순서 변경
    void updateFaqOrder(Long id, int newOrder);
    
    // FAQ 활성화/비활성화
    void toggleFaqStatus(Long id);

    // FAQ 순서 변경 (컨트롤러용)
    void changeFaqOrder(Long id, int newOrder);
} 