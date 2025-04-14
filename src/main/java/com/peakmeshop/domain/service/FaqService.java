package com.peakmeshop.domain.service;

import com.peakmeshop.domain.entity.Faq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FaqService {
    
    // FAQ 목록 조회
    Page<Faq> getFaqList(Pageable pageable);
    
    // 카테고리별 FAQ 목록 조회
    Page<Faq> getFaqListByCategory(String category, Pageable pageable);

    // FAQ 상세 조회
    Faq getFaqById(Long id);
    
    // FAQ 생성
    Faq createFaq(Faq faq);
    
    // FAQ 수정
    Faq updateFaq(Long id, Faq faq);
    
    // FAQ 삭제
    void deleteFaq(Long id);
    
    // FAQ 순서 변경
    void updateFaqOrder(Long id, int newOrder);
    
    // FAQ 활성화/비활성화
    void toggleFaqStatus(Long id);

    // FAQ 순서 변경 (컨트롤러용)
    void changeFaqOrder(Long id, int newOrder);
} 