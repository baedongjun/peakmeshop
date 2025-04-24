package com.peakmeshop.domain.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.peakmeshop.api.dto.NoticeDTO;
import com.peakmeshop.api.dto.FaqDTO;
import com.peakmeshop.domain.service.ContentService;
import com.peakmeshop.domain.repository.NoticeRepository;
import com.peakmeshop.domain.repository.FaqRepository;
import com.peakmeshop.api.mapper.NoticeMapper;
import com.peakmeshop.api.mapper.FaqMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final NoticeRepository noticeRepository;
    private final FaqRepository faqRepository;
    private final NoticeMapper noticeMapper;
    private final FaqMapper faqMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<NoticeDTO> getNotices(String category, String status, Pageable pageable) {
        return noticeRepository.findByCategoryAndStatus(category, status, pageable)
                .map(noticeMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FaqDTO> getFaqs(String category, Pageable pageable) {
        return faqRepository.findByCategory(category, pageable)
                .map(faqMapper::toDTO);
    }
} 