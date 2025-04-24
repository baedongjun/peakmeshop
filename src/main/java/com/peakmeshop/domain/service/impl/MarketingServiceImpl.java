package com.peakmeshop.domain.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.peakmeshop.api.dto.CouponDTO;
import com.peakmeshop.api.dto.PromotionDTO;
import com.peakmeshop.domain.service.MarketingService;
import com.peakmeshop.domain.repository.CouponRepository;
import com.peakmeshop.domain.repository.PromotionRepository;
import com.peakmeshop.api.mapper.CouponMapper;
import com.peakmeshop.api.mapper.PromotionMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarketingServiceImpl implements MarketingService {

    private final CouponRepository couponRepository;
    private final PromotionRepository promotionRepository;
    private final CouponMapper couponMapper;
    private final PromotionMapper promotionMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<CouponDTO> getCoupons(String status, Pageable pageable) {
        return couponRepository.findByStatus(status, pageable)
                .map(couponMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PromotionDTO> getPromotions(Boolean isActive, Pageable pageable) {
        return promotionRepository.findByIsActive(isActive, pageable)
                .map(promotionMapper::toDTO);
    }
} 