package com.peakmeshop.domain.service.impl;

import com.peakmeshop.domain.service.InquiryService;
import com.peakmeshop.domain.entity.Inquiry;
import com.peakmeshop.domain.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;

    @Override
    public Page<Inquiry> getInquiryList(Pageable pageable) {
        return inquiryRepository.findAll(pageable);
    }

    @Override
    public Page<Inquiry> getInquiryListByCategory(String category, Pageable pageable) {
        return inquiryRepository.findByCategory(category, pageable);
    }

    @Override
    public Page<Inquiry> getInquiryListByStatus(Inquiry.InquiryStatus status, Pageable pageable) {
        return inquiryRepository.findByStatus(status, pageable);
    }

    @Override
    public Inquiry getInquiryById(Long id) {
        return inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inquiry not found with id: " + id));
    }

    @Override
    public Inquiry answerInquiry(Long id, String answer) {
        Inquiry inquiry = getInquiryById(id);
        inquiry.setAnswer(answer);
        inquiry.setAnsweredAt(LocalDateTime.now());
        inquiry.setStatus(Inquiry.InquiryStatus.ANSWERED);
        return inquiryRepository.save(inquiry);
    }

    @Override
    public Inquiry updateInquiryStatus(Long id, Inquiry.InquiryStatus status) {
        Inquiry inquiry = getInquiryById(id);
        inquiry.setStatus(status);
        return inquiryRepository.save(inquiry);
    }

    @Override
    public void deleteInquiry(Long id) {
        Inquiry inquiry = getInquiryById(id);
        inquiryRepository.delete(inquiry);
    }

    @Override
    public Page<Inquiry> getInquiries(String category, Inquiry.InquiryStatus status, Pageable pageable) {
        if (category != null && status != null) {
            return inquiryRepository.findByCategoryAndStatus(category, status, pageable);
        } else if (category != null) {
            return inquiryRepository.findByCategory(category, pageable);
        } else if (status != null) {
            return inquiryRepository.findByStatus(status, pageable);
        } else {
            return inquiryRepository.findAll(pageable);
        }
    }

    @Override
    public Inquiry getInquiry(Long id) {
        return getInquiryById(id);
    }

    @Override
    public Inquiry changeInquiryStatus(Long id, Inquiry.InquiryStatus status) {
        return updateInquiryStatus(id, status);
    }
} 