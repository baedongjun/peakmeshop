package com.peakmeshop.domain.service.impl;

import com.peakmeshop.api.dto.PromotionDTO;
import com.peakmeshop.api.dto.PromotionSummaryDTO;
import com.peakmeshop.api.mapper.PromotionMapper;
import com.peakmeshop.domain.entity.Category;
import com.peakmeshop.domain.entity.Product;
import com.peakmeshop.domain.entity.Promotion;
import com.peakmeshop.domain.repository.CategoryRepository;
import com.peakmeshop.domain.repository.ProductRepository;
import com.peakmeshop.domain.repository.PromotionRepository;
import com.peakmeshop.domain.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PromotionMapper promotionMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<PromotionDTO> getAllPromotions(Pageable pageable) {
        return promotionRepository.findAll(pageable)
                .map(promotionMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PromotionDTO> getActivePromotions(Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        return promotionRepository.findByStartDateBeforeAndEndDateAfterAndIsActiveTrue(now, now, pageable)
                .map(promotionMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PromotionDTO> getPromotionById(Long id) {
        return promotionRepository.findById(id)
                .map(promotionMapper::toDTO);
    }

    @Override
    @Transactional
    public PromotionDTO createPromotion(PromotionDTO promotionDTO) {
        Promotion promotion = promotionMapper.toEntity(promotionDTO);
        promotion.setCreatedAt(LocalDateTime.now());
        
        if (promotionDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(promotionDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + promotionDTO.getCategoryId()));
            promotion.setCategory(category);
        }
        
        if (promotionDTO.getProductId() != null) {
            Product product = productRepository.findById(promotionDTO.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + promotionDTO.getProductId()));
            promotion.setProduct(product);
        }
        
        Promotion savedPromotion = promotionRepository.save(promotion);
        return promotionMapper.toDTO(savedPromotion);
    }

    @Override
    @Transactional
    public PromotionDTO updatePromotion(Long id, PromotionDTO promotionDTO) {
        if (!promotionRepository.existsById(id)) {
            throw new IllegalArgumentException("Promotion not found with id: " + id);
        }

        Promotion promotion = promotionMapper.toEntity(promotionDTO);
        promotion.setId(id);
        promotion.setUpdatedAt(LocalDateTime.now());

        if (promotionDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(promotionDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + promotionDTO.getCategoryId()));
            promotion.setCategory(category);
        }
        
        if (promotionDTO.getProductId() != null) {
            Product product = productRepository.findById(promotionDTO.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + promotionDTO.getProductId()));
            promotion.setProduct(product);
        }

        Promotion updatedPromotion = promotionRepository.save(promotion);
        return promotionMapper.toDTO(updatedPromotion);
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
                .map(promotionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionDTO> getCurrentPromotions() {
        LocalDateTime now = LocalDateTime.now();
        return promotionRepository.findByStartDateBeforeAndEndDateAfterAndIsActiveTrue(now, now)
                .stream()
                .map(promotionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PromotionDTO> getPromotionByCode(String code) {
        return promotionRepository.findByPromotionCode(code)
                .map(promotionMapper::toDTO);
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
    @Transactional(readOnly = true)
    public Page<PromotionDTO> getPromotions(String keyword, Pageable pageable) {
        Page<Promotion> promotions;
        if (keyword != null && !keyword.isEmpty()) {
            promotions = promotionRepository.searchPromotions(keyword, pageable);
        } else {
            promotions = promotionRepository.findAll(pageable);
        }
        return promotions.map(promotionMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionSummaryDTO getPromotionSummary() {
        long totalPromotions = promotionRepository.count();
        long activePromotions = promotionRepository.countByIsActiveTrue();

        return PromotionSummaryDTO.builder()
                .totalPromotions(totalPromotions)
                .activePromotions(activePromotions)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PromotionDTO> getPromotion(Long id) {
        return promotionRepository.findById(id)
                .map(promotionMapper::toDTO);
    }

    @Override
    @Transactional
    public void startPromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promotion not found with id: " + id));
        promotion.setActive(true);
        promotionRepository.save(promotion);
    }

    @Override
    @Transactional
    public void endPromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promotion not found with id: " + id));
        promotion.setActive(false);
        promotionRepository.save(promotion);
    }

    @Override
    @Transactional
    public void suspendPromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("프로모션을 찾을 수 없습니다: " + id));
        promotion.setActive(false);
        promotion.setSuspended(true);
        promotionRepository.save(promotion);
    }

    @Override
    @Transactional
    public void resumePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promotion not found with id: " + id));
        promotion.setActive(true);
        promotionRepository.save(promotion);
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionSummaryDTO getPromotionSummary(String period, String startDate, String endDate) {
        long totalPromotions = promotionRepository.count();
        long activePromotions = promotionRepository.countByIsActiveTrue();

        return PromotionSummaryDTO.builder()
                .totalPromotions(totalPromotions)
                .activePromotions(activePromotions)
                .build();
    }
}