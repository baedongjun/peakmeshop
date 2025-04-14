package com.peakmeshop.domain.service.impl;

import com.peakmeshop.domain.service.FaqService;
import com.peakmeshop.domain.entity.Faq;
import com.peakmeshop.domain.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FaqServiceImpl implements FaqService {

    private final FaqRepository faqRepository;

    @Override
    public Page<Faq> getFaqList(Pageable pageable) {
        return faqRepository.findAll(pageable);
    }

    @Override
    public Page<Faq> getFaqListByCategory(String category, Pageable pageable) {
        return faqRepository.findByCategory(category, pageable);
    }

    @Override
    public Faq getFaqById(Long id) {
        return faqRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FAQ not found with id: " + id));
    }

    @Override
    public Faq createFaq(Faq faq) {
        // 새로운 FAQ의 순서를 마지막으로 설정
        int maxOrder = faqRepository.findMaxSortOrder();
        faq.setSortOrder(maxOrder + 1);
        return faqRepository.save(faq);
    }

    @Override
    public Faq updateFaq(Long id, Faq faq) {
        Faq existingFaq = getFaqById(id);
        existingFaq.setQuestion(faq.getQuestion());
        existingFaq.setAnswer(faq.getAnswer());
        existingFaq.setCategory(faq.getCategory());
        existingFaq.setActive(faq.isActive());
        return faqRepository.save(existingFaq);
    }

    @Override
    public void deleteFaq(Long id) {
        Faq faq = getFaqById(id);
        faqRepository.delete(faq);
    }

    @Override
    public void updateFaqOrder(Long id, int newOrder) {
        Faq faq = getFaqById(id);
        int oldOrder = faq.getSortOrder();
        
        if (newOrder > oldOrder) {
            faqRepository.decreaseOrderBetween(oldOrder + 1, newOrder);
        } else if (newOrder < oldOrder) {
            faqRepository.increaseOrderBetween(newOrder, oldOrder - 1);
        }
        
        faq.setSortOrder(newOrder);
        faqRepository.save(faq);
    }

    @Override
    public void toggleFaqStatus(Long id) {
        Faq faq = getFaqById(id);
        faq.setActive(!faq.isActive());
        faqRepository.save(faq);
    }

    @Override
    public void changeFaqOrder(Long id, int newOrder) {
        updateFaqOrder(id, newOrder);
    }
} 