package com.peakmeshop.domain.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.PromotionDTO;
import com.peakmeshop.api.dto.PromotionSummaryDTO;

public interface PromotionService {

    Page<PromotionDTO> getAllPromotions(Pageable pageable);

    Page<PromotionDTO> getActivePromotions(Pageable pageable);

    Optional<PromotionDTO> getPromotionById(Long id);

    PromotionDTO createPromotion(PromotionDTO promotionDTO);

    PromotionDTO updatePromotion(Long id, PromotionDTO promotionDTO);

    boolean deletePromotion(Long id);

    List<PromotionDTO> getPromotionsByCategory(Long categoryId);

    List<PromotionDTO> getCurrentPromotions();

    Optional<PromotionDTO> getPromotionByCode(String code);

    boolean isPromotionValid(String code);

    Page<PromotionDTO> getPromotions(String keyword, Pageable pageable);

    PromotionSummaryDTO getPromotionSummary();

    Optional<PromotionDTO> getPromotion(Long id);

    void startPromotion(Long id);

    void endPromotion(Long id);

    void suspendPromotion(Long id);

    void resumePromotion(Long id);

    PromotionSummaryDTO getPromotionSummary(String period, String startDate, String endDate);
}