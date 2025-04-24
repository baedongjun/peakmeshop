package com.peakmeshop.domain.service.impl;

import com.peakmeshop.domain.service.InquiryService;
import com.peakmeshop.domain.entity.Inquiry;
import com.peakmeshop.domain.repository.InquiryRepository;
import com.peakmeshop.api.mapper.InquiryMapper;
import com.peakmeshop.api.dto.InquiryDTO;
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
    private final InquiryMapper inquiryMapper;

    @Override
    public Page<InquiryDTO> getInquiryList(Pageable pageable) {
        return inquiryRepository.findAll(pageable)
                .map(inquiryMapper::toDTO);
    }

    @Override
    public Page<InquiryDTO> getInquiryListByCategory(String category, Pageable pageable) {
        return inquiryRepository.findByCategory(category, pageable)
                .map(inquiryMapper::toDTO);
    }

    @Override
    public Page<InquiryDTO> getInquiryListByStatus(Inquiry.InquiryStatus status, Pageable pageable) {
        return inquiryRepository.findByStatus(status, pageable)
                .map(inquiryMapper::toDTO);
    }

    @Override
    public InquiryDTO getInquiryById(Long id) {
        return inquiryMapper.toDTO(inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inquiry not found with id: " + id)));
    }

    @Override
    public InquiryDTO answerInquiry(Long id, String answer) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inquiry not found with id: " + id));
        inquiry.setAnswer(answer);
        inquiry.setAnsweredAt(LocalDateTime.now());
        inquiry.setStatus(Inquiry.InquiryStatus.ANSWERED);
        return inquiryMapper.toDTO(inquiryRepository.save(inquiry));
    }

    @Override
    public InquiryDTO updateInquiryStatus(Long id, Inquiry.InquiryStatus status) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inquiry not found with id: " + id));
        inquiry.setStatus(status);
        return inquiryMapper.toDTO(inquiryRepository.save(inquiry));
    }

    @Override
    public void deleteInquiry(Long id) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inquiry not found with id: " + id));
        inquiryRepository.delete(inquiry);
    }

    @Override
    public Page<InquiryDTO> getInquiries(String category, Inquiry.InquiryStatus status, Pageable pageable) {
        if (category != null && status != null) {
            return inquiryRepository.findByCategoryAndStatus(category, status, pageable)
                    .map(inquiryMapper::toDTO);
        } else if (category != null) {
            return inquiryRepository.findByCategory(category, pageable)
                    .map(inquiryMapper::toDTO);
        } else if (status != null) {
            return inquiryRepository.findByStatus(status, pageable)
                    .map(inquiryMapper::toDTO);
        } else {
            return inquiryRepository.findAll(pageable)
                    .map(inquiryMapper::toDTO);
        }
    }

    @Override
    public InquiryDTO getInquiry(Long id) {
        return getInquiryById(id);
    }

    @Override
    public InquiryDTO changeInquiryStatus(Long id, Inquiry.InquiryStatus status) {
        return updateInquiryStatus(id, status);
    }
} 