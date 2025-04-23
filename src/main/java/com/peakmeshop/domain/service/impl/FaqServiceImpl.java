package com.peakmeshop.domain.service.impl;

import com.peakmeshop.api.dto.FaqDTO;
import com.peakmeshop.api.mapper.FaqMapper;
import com.peakmeshop.domain.service.FaqService;
import com.peakmeshop.domain.entity.Faq;
import com.peakmeshop.domain.repository.FaqRepository;
import jakarta.persistence.EntityNotFoundException;
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
    private final FaqMapper faqMapper;

    @Override
    public Page<FaqDTO> getFaqList(Pageable pageable) {
        return faqRepository.findAll(pageable).map(faqMapper::toDTO);
    }

    @Override
    public Page<FaqDTO> getFaqListByCategory(String category, Pageable pageable) {
        return faqRepository.findByCategory(category, pageable).map(faqMapper::toDTO);
    }

    @Override
    public FaqDTO getFaqById(Long id) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("FAQ를 찾을 수 없습니다. ID: " + id));
        return faqMapper.toDTO(faq);
    }

    @Override
    public FaqDTO createFaq(Faq faq) {
        // 새로운 FAQ의 순서를 마지막으로 설정
        Integer maxOrder = faqRepository.findMaxSortOrder();
        int newOrder = (maxOrder == null) ? 1 : maxOrder + 1;
        
        faq.setSortOrder(newOrder);
        faq.setActive(true);
        
        return faqMapper.toDTO(faqRepository.save(faq));
    }

    @Override
    public FaqDTO updateFaq(Long id, Faq faq) {
        return faqMapper.toDTO(faqRepository.save(faq));
    }

    @Override
    public void deleteFaq(Long id) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("FAQ를 찾을 수 없습니다. ID: " + id));
        faqRepository.delete(faq);
    }

    @Override
    public void updateFaqOrder(Long id, int newOrder) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("FAQ를 찾을 수 없습니다. ID: " + id));
        
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
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("FAQ를 찾을 수 없습니다. ID: " + id));
        faq.setActive(!faq.getIsActive());
        faqRepository.save(faq);
    }

    @Override
    public void changeFaqOrder(Long id, int newOrder) {
        updateFaqOrder(id, newOrder);
    }
} 