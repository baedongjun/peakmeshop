package com.peakmeshop.domain.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.peakmeshop.api.dto.PromotionSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.PromotionDTO;
import com.peakmeshop.domain.entity.Category;
import com.peakmeshop.domain.entity.Promotion;
import com.peakmeshop.domain.repository.CategoryRepository;
import com.peakmeshop.domain.repository.PromotionRepository;
import com.peakmeshop.domain.service.PromotionService;

@Service
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final CategoryRepository categoryRepository;

    public PromotionServiceImpl(
            PromotionRepository promotionRepository,
            CategoryRepository categoryRepository) {
        this.promotionRepository = promotionRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PromotionDTO> getAllPromotions(Pageable pageable) {
        return promotionRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PromotionDTO> getActivePromotions(Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        return promotionRepository.findByStartDateBeforeAndEndDateAfterAndIsActiveTrue(now, now, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PromotionDTO> getPromotionById(Long id) {
        return promotionRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public PromotionDTO createPromotion(PromotionDTO promotionDTO) {
        Promotion promotion = new Promotion();
        promotion.setName(promotionDTO.getName());
        promotion.setDescription(promotionDTO.getDescription());
        promotion.setDiscountType(promotionDTO.getDiscountType());
        promotion.setDiscountValue(promotionDTO.getDiscountValue());
        promotion.setStartDate(promotionDTO.getStartDate());
        promotion.setEndDate(promotionDTO.getEndDate());
        promotion.setActive(promotionDTO.getIsActive());
        promotion.setBannerImageUrl(promotionDTO.getBannerImageUrl());
        promotion.setPromotionCode(promotionDTO.getPromotionCode());
        promotion.setCreatedAt(LocalDateTime.now());
        promotion.setUpdatedAt(LocalDateTime.now());

        // 카테고리 설정
        if (promotionDTO.getCategory() != null) {
            Category category = categoryRepository.findById(promotionDTO.getCategory().getId())
                    .orElseThrow(() -> new UsernameNotFoundException("카테고리를 찾을 수 없습니다. ID: " + promotionDTO.getCategory().getId()));
            promotion.setCategory(category);
        }

        Promotion savedPromotion = promotionRepository.save(promotion);
        return convertToDTO(savedPromotion);
    }

    @Override
    @Transactional
    public PromotionDTO updatePromotion(Long id, PromotionDTO promotionDTO) {
        Promotion existingPromotion = promotionRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("프로모션을 찾을 수 없습니다. ID: " + id));

        existingPromotion.setName(promotionDTO.getName());
        existingPromotion.setDescription(promotionDTO.getDescription());
        existingPromotion.setDiscountType(promotionDTO.getDiscountType());
        existingPromotion.setDiscountValue(promotionDTO.getDiscountValue());
        existingPromotion.setStartDate(promotionDTO.getStartDate());
        existingPromotion.setEndDate(promotionDTO.getEndDate());
        existingPromotion.setActive(promotionDTO.getIsActive());
        existingPromotion.setBannerImageUrl(promotionDTO.getBannerImageUrl());
        existingPromotion.setPromotionCode(promotionDTO.getPromotionCode());
        existingPromotion.setUpdatedAt(LocalDateTime.now());

        // 카테고리 설정
        if (promotionDTO.getCategory() != null) {
            Category category = categoryRepository.findById(promotionDTO.getCategory().getId())
                    .orElseThrow(() -> new UsernameNotFoundException("카테고리를 찾을 수 없습니다. ID: " + promotionDTO.getCategory().getId()));
            existingPromotion.setCategory(category);
        } else {
            existingPromotion.setCategory(null);
        }

        Promotion updatedPromotion = promotionRepository.save(existingPromotion);
        return convertToDTO(updatedPromotion);
    }

    @Override
    @Transactional
    public boolean deletePromotion(Long id) {
        if (promotionRepository.existsById(id)) {
            promotionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionDTO> getPromotionsByCategory(Long categoryId) {
        return promotionRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionDTO> getCurrentPromotions() {
        LocalDateTime now = LocalDateTime.now();
        return promotionRepository.findByStartDateBeforeAndEndDateAfterAndIsActiveTrue(now, now)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PromotionDTO> getPromotionByCode(String code) {
        return promotionRepository.findByPromotionCode(code)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPromotionValid(String code) {
        Optional<Promotion> promotionOpt = promotionRepository.findByPromotionCode(code);

        if (promotionOpt.isPresent()) {
            Promotion promotion = promotionOpt.get();
            LocalDateTime now = LocalDateTime.now();

            return promotion.isActive() &&
                    now.isAfter(promotion.getStartDate()) &&
                    now.isBefore(promotion.getEndDate());
        }

        return false;
    }

    @Override
    public Page<PromotionDTO> getPromotions(String type, String status, String keyword, Pageable pageable) {
        return null;
    }

    @Override
    public PromotionSummaryDTO getPromotionSummary() {
        return null;
    }

    @Override
    public Optional<PromotionDTO> getPromotion(Long id) {
        return Optional.empty();
    }

    @Override
    public void startPromotion(Long id) {

    }

    @Override
    public void endPromotion(Long id) {

    }

    @Override
    public void suspendPromotion(Long id) {

    }

    @Override
    public void resumePromotion(Long id) {

    }

    // 엔티티를 DTO로 변환
    private PromotionDTO convertToDTO(Promotion promotion) {
        return PromotionDTO.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .description(promotion.getDescription())
                .discountType(promotion.getDiscountType())
                .discountValue(promotion.getDiscountValue())
                .target(promotion.getTarget())
                .category(promotion.getCategory())
                .product(promotion.getProduct())
                .discountRate(promotion.getDiscountRate())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .isActive(promotion.isActive())
                .bannerImageUrl(promotion.getBannerImageUrl())
                .promotionCode(promotion.getPromotionCode())
                .createdAt(promotion.getCreatedAt())
                .updatedAt(promotion.getUpdatedAt())
                .build();
    }
}