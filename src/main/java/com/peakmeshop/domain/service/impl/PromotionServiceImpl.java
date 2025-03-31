package com.peakmeshop.domain.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.PromotionDTO;
import com.peakmeshop.domain.entity.Category;
import com.peakmeshop.domain.entity.Promotion;
import com.peakmeshop.common.exception.ResourceNotFoundException;
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
        return promotionRepository.findByStartDateBeforeAndEndDateAfterAndActiveTrue(now, now, pageable)
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
        promotion.setName(promotionDTO.name());
        promotion.setDescription(promotionDTO.description());
        promotion.setDiscountType(promotionDTO.discountType());
        promotion.setDiscountValue(promotionDTO.discountValue());
        promotion.setStartDate(promotionDTO.startDate());
        promotion.setEndDate(promotionDTO.endDate());
        promotion.setActive(promotionDTO.isActive());
        promotion.setBannerImageUrl(promotionDTO.bannerImageUrl());
        promotion.setPromotionCode(promotionDTO.promotionCode());
        promotion.setCreatedAt(LocalDateTime.now());
        promotion.setUpdatedAt(LocalDateTime.now());

        // 카테고리 설정
        if (promotionDTO.categoryId() != null) {
            Category category = categoryRepository.findById(promotionDTO.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("카테고리를 찾을 수 없습니다. ID: " + promotionDTO.categoryId()));
            promotion.setCategory(category);
        }

        Promotion savedPromotion = promotionRepository.save(promotion);
        return convertToDTO(savedPromotion);
    }

    @Override
    @Transactional
    public PromotionDTO updatePromotion(Long id, PromotionDTO promotionDTO) {
        Promotion existingPromotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("프로모션을 찾을 수 없습니다. ID: " + id));

        existingPromotion.setName(promotionDTO.name());
        existingPromotion.setDescription(promotionDTO.description());
        existingPromotion.setDiscountType(promotionDTO.discountType());
        existingPromotion.setDiscountValue(promotionDTO.discountValue());
        existingPromotion.setStartDate(promotionDTO.startDate());
        existingPromotion.setEndDate(promotionDTO.endDate());
        existingPromotion.setActive(promotionDTO.isActive());
        existingPromotion.setBannerImageUrl(promotionDTO.bannerImageUrl());
        existingPromotion.setPromotionCode(promotionDTO.promotionCode());
        existingPromotion.setUpdatedAt(LocalDateTime.now());

        // 카테고리 설정
        if (promotionDTO.categoryId() != null) {
            Category category = categoryRepository.findById(promotionDTO.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("카테고리를 찾을 수 없습니다. ID: " + promotionDTO.categoryId()));
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
        return promotionRepository.findByStartDateBeforeAndEndDateAfterAndActiveTrue(now, now)
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

    // 엔티티를 DTO로 변환
    private PromotionDTO convertToDTO(Promotion promotion) {
        return new PromotionDTO(
                promotion.getId(),
                promotion.getName(),
                promotion.getDescription(),
                promotion.getDiscountType(),
                promotion.getDiscountValue(),
                promotion.getStartDate(),
                promotion.getEndDate(),
                promotion.isActive(),
                promotion.getBannerImageUrl(),
                promotion.getPromotionCode(),
                promotion.getCategory() != null ? promotion.getCategory().getId() : null,
                promotion.getCategory() != null ? promotion.getCategory().getName() : null,
                promotion.getCreatedAt(),
                promotion.getUpdatedAt()
        );
    }
}