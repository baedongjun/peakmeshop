package com.peakmeshop.domain.service.impl;

import com.peakmeshop.api.dto.PromotionDTO;
import com.peakmeshop.api.dto.PromotionSummaryDTO;
import com.peakmeshop.domain.entity.Category;
import com.peakmeshop.domain.entity.Product;
import com.peakmeshop.domain.entity.Promotion;
import com.peakmeshop.domain.repository.CategoryRepository;
import com.peakmeshop.domain.repository.ProductRepository;
import com.peakmeshop.domain.repository.PromotionRepository;
import com.peakmeshop.domain.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

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
        Promotion promotion = convertToEntity(promotionDTO);
        promotion.setCreatedAt(LocalDateTime.now());
        Promotion savedPromotion = promotionRepository.save(promotion);
        return convertToDTO(savedPromotion);
    }

    @Override
    @Transactional
    public PromotionDTO updatePromotion(Long id, PromotionDTO promotionDTO) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promotion not found with id: " + id));

        promotion.setName(promotionDTO.getName());
        promotion.setDescription(promotionDTO.getDescription());
        promotion.setDiscountType(promotionDTO.getDiscountType());
        promotion.setDiscountValue(promotionDTO.getDiscountValue());
        promotion.setTarget(promotionDTO.getTarget());
        if (promotionDTO.getCategory() != null) {
            Category category = categoryRepository.findById(promotionDTO.getCategory().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + promotionDTO.getCategory().getId()));
            promotion.setCategory(category);
        }
        if (promotionDTO.getProduct() != null) {
            Product product = productRepository.findById(promotionDTO.getProduct().getId()).orElse(null);
            promotion.setProduct(product);
        }
        promotion.setDiscountRate(promotionDTO.getDiscountRate());
        promotion.setStartDate(promotionDTO.getStartDate());
        promotion.setEndDate(promotionDTO.getEndDate());
        promotion.setActive(promotionDTO.getIsActive());
        promotion.setBannerImageUrl(promotionDTO.getBannerImageUrl());
        promotion.setPromotionCode(promotionDTO.getPromotionCode());
        promotion.setUpdatedAt(LocalDateTime.now());

        Promotion updatedPromotion = promotionRepository.save(promotion);
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
    @Transactional(readOnly = true)
    public Page<PromotionDTO> getPromotions(String keyword, Pageable pageable) {
        Page<Promotion> promotions;
        if (keyword != null && !keyword.isEmpty()) {
            promotions = promotionRepository.searchPromotions(keyword, pageable);
        } else {
            promotions = promotionRepository.findAll(pageable);
        }
        return promotions.map(this::convertToDTO);
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
                .map(this::convertToDTO);
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
                .orElseThrow(() -> new IllegalArgumentException("Promotion not found with id: " + id));
        promotion.setActive(false);
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
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (startDate != null && endDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localStartDate = LocalDate.parse(startDate, formatter);
            LocalDate localEndDate = LocalDate.parse(endDate, formatter);
            start = localStartDate.atStartOfDay();
            end = localEndDate.plusDays(1).atStartOfDay();
        } else {
            LocalDateTime now = LocalDateTime.now();
            if (period != null) {
                switch (period) {
                    case "daily":
                        start = now.toLocalDate().atStartOfDay();
                        end = now.plusDays(1).toLocalDate().atStartOfDay();
                        break;
                    case "weekly":
                        start = now.minusWeeks(1).toLocalDate().atStartOfDay();
                        end = now.plusDays(1).toLocalDate().atStartOfDay();
                        break;
                    case "monthly":
                        start = now.minusMonths(1).toLocalDate().atStartOfDay();
                        end = now.plusDays(1).toLocalDate().atStartOfDay();
                        break;
                    case "yearly":
                        start = now.minusYears(1).toLocalDate().atStartOfDay();
                        end = now.plusDays(1).toLocalDate().atStartOfDay();
                        break;
                    default:
                        start = now.minusMonths(1).toLocalDate().atStartOfDay();
                        end = now.plusDays(1).toLocalDate().atStartOfDay();
                }
            } else {
                start = now.minusMonths(1).toLocalDate().atStartOfDay();
                end = now.plusDays(1).toLocalDate().atStartOfDay();
            }
        }

        long totalPromotions = promotionRepository.countByCreatedAtBetween(start, end);
        long activePromotions = promotionRepository.countByIsActiveTrueAndCreatedAtBetween(start, end);

        return PromotionSummaryDTO.builder()
                .totalPromotions(totalPromotions)
                .activePromotions(activePromotions)
                .build();
    }

    private Promotion convertToEntity(PromotionDTO dto) {
        Promotion promotion = new Promotion();
        promotion.setId(dto.getId());
        promotion.setName(dto.getName());
        promotion.setDescription(dto.getDescription());
        promotion.setDiscountType(dto.getDiscountType());
        promotion.setDiscountValue(dto.getDiscountValue());
        promotion.setTarget(dto.getTarget());
        if (dto.getCategory() != null) {
            promotion.setCategory(categoryRepository.findById(dto.getCategory().getId()).orElse(null));
        }
        if (dto.getProduct() != null) {
            promotion.setProduct(productRepository.findById(dto.getProduct().getId()).orElse(null));
        }
        promotion.setDiscountRate(dto.getDiscountRate());
        promotion.setStartDate(dto.getStartDate());
        promotion.setEndDate(dto.getEndDate());
        promotion.setActive(dto.getIsActive());
        promotion.setBannerImageUrl(dto.getBannerImageUrl());
        promotion.setPromotionCode(dto.getPromotionCode());
        if (dto.getCreatedAt() != null) {
            promotion.setCreatedAt(dto.getCreatedAt());
        }
        if (dto.getUpdatedAt() != null) {
            promotion.setUpdatedAt(dto.getUpdatedAt());
        }
        return promotion;
    }

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