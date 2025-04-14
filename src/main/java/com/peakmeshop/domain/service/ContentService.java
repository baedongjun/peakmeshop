package com.peakmeshop.domain.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.peakmeshop.api.dto.NoticeDTO;
import com.peakmeshop.api.dto.FaqDTO;

public interface ContentService {
    Page<NoticeDTO> getNotices(String category, String status, Pageable pageable);
    Page<FaqDTO> getFaqs(String category, Pageable pageable);
    // ... existing code ...
} 