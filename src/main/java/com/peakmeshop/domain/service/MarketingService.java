package com.peakmeshop.domain.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.peakmeshop.api.dto.CouponDTO;
import com.peakmeshop.api.dto.PromotionDTO;

public interface MarketingService {
    Page<CouponDTO> getCoupons(String status, Pageable pageable);
    Page<PromotionDTO> getPromotions(Boolean isActive, Pageable pageable);
    // ... existing code ...
} 