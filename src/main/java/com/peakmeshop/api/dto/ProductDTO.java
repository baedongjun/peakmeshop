package com.peakmeshop.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.peakmeshop.domain.entity.Brand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;
    private String code; // 상품 코드
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal salePrice; // 판매가 (할인가)
    private BigDecimal discountedPrice; // 할인가 (다른 이름)
    private Brand brand; // 브랜드
    private Long categoryId;
    private String categoryName;
    private String mainImage;
    private List<String> images;
    private Integer stock;
    private String status; // 상품 상태 (ACTIVE, INACTIVE, OUT_OF_STOCK 등)
    private Boolean active; // 활성화 여부
    private Boolean featured; // 추천 상품 여부
    private Double averageRating;
    private Integer reviewCount;
    private Integer salesCount; // 판매 수량
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>(); // 상품 속성

    @Builder.Default
    private List<ProductOptionDTO> options = new ArrayList<>();

    // 할인율 계산
    public BigDecimal getDiscountRate() {
        if (price == null || salePrice == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = price.subtract(salePrice);
        return discount.multiply(new BigDecimal(100)).divide(price, 0, BigDecimal.ROUND_HALF_UP);
    }

    // 할인 금액 계산
    public BigDecimal getDiscountAmount() {
        if (price == null || salePrice == null) {
            return BigDecimal.ZERO;
        }

        return price.subtract(salePrice);
    }

    // 활성화 여부 확인
    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    // 추천 상품 여부 확인
    public boolean isFeatured() {
        return Boolean.TRUE.equals(featured);
    }
}