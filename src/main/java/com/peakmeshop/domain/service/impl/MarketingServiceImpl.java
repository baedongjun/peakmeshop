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
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarketingServiceImpl implements MarketingService {

    private final CouponRepository couponRepository;
    private final PromotionRepository promotionRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<CouponDTO> getCoupons(String status, Pageable pageable) {
        return couponRepository.findByStatus(status, pageable)
                .map(coupon -> CouponDTO.builder()
                        .id(coupon.getId())
                        .code(coupon.getCode())
                        .name(coupon.getName())
                        .description(coupon.getDescription())
                        .discountType(coupon.getDiscountType())
                        .discountValue(coupon.getDiscountValue())
                        .minOrderAmount(coupon.getMinOrderAmount())
                        .maxDiscountAmount(coupon.getMaxDiscountAmount())
                        .startDate(coupon.getStartDate())
                        .endDate(coupon.getEndDate())
                        .usageLimit(coupon.getUsageLimit())
                        .usedCount(coupon.getUsedCount())
                        .status(coupon.getStatus())
                        .createdAt(coupon.getCreatedAt())
                        .updatedAt(coupon.getUpdatedAt())
                        .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PromotionDTO> getPromotions(Boolean isActive, Pageable pageable) {
        return promotionRepository.findByIsActive(isActive, pageable)
                .map(promotion -> PromotionDTO.builder()
                        .id(promotion.getId())
                        .name(promotion.getName())
                        .description(promotion.getDescription())
                        .discountType(promotion.getDiscountType())
                        .discountValue(promotion.getDiscountValue())
                        .target(promotion.getTarget())
                        .categoryId(promotion.getCategory().getId())
                        .productId(promotion.getProduct().getId())
                        .discountRate(promotion.getDiscountRate())
                        .startDate(promotion.getStartDate())
                        .endDate(promotion.getEndDate())
                        .isActive(promotion.isActive())
                        .bannerImageUrl(promotion.getBannerImageUrl())
                        .promotionCode(promotion.getPromotionCode())
                        .createdAt(promotion.getCreatedAt())
                        .updatedAt(promotion.getUpdatedAt())
                        .build());
    }
} 