package com.peakmeshop.api.dto;

import com.peakmeshop.domain.entity.Category;
import com.peakmeshop.domain.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionDTO {
    private Long id;
    private String name;
    private String description;
    private String discountType;
    private Double discountValue;
    private String target;
    private Long categoryId;
    private Long productId;
    private Double discountRate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
    private String bannerImageUrl;
    private String promotionCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}