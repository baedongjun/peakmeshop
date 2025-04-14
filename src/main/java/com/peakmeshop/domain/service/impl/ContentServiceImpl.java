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
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final NoticeRepository noticeRepository;
    private final FaqRepository faqRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<NoticeDTO> getNotices(String category, String status, Pageable pageable) {
        return noticeRepository.findByCategoryAndStatus(category, status, pageable)
                .map(notice -> NoticeDTO.builder()
                        .id(notice.getId())
                        .title(notice.getTitle())
                        .content(notice.getContent())
                        .category(notice.getCategory())
                        .status(notice.getStatus())
                        .viewCount(notice.getViewCount())
                        .isImportant(notice.isImportant())
                        .isTop(notice.isTop())
                        .startDate(notice.getStartDate())
                        .endDate(notice.getEndDate())
                        .createdAt(notice.getCreatedAt())
                        .updatedAt(notice.getUpdatedAt())
                        .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FaqDTO> getFaqs(String category, Pageable pageable) {
        return faqRepository.findByCategory(category, pageable)
                .map(faq -> FaqDTO.builder()
                        .id(faq.getId())
                        .question(faq.getQuestion())
                        .answer(faq.getAnswer())
                        .category(faq.getCategory())
                        .sortOrder(faq.getSortOrder())
                        .isActive(faq.isActive())
                        .createdAt(faq.getCreatedAt())
                        .updatedAt(faq.getUpdatedAt())
                        .build());
    }
} 