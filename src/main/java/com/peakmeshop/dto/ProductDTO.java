package com.peakmeshop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * 상품 정보를 전송하기 위한 DTO
 * Java 17의 Record 기능을 사용하여 불변 객체로 구현
 */
public record ProductDTO(
        Long id,

        @NotBlank(message = "상품명은 필수입니다")
        @Size(min = 2, max = 100, message = "상품명은 2-100자 사이여야 합니다")
        String name,

        @Size(max = 1000, message = "상품 설명은 1000자를 초과할 수 없습니다")
        String description,

        @NotNull(message = "가격은 필수입니다")
        @Positive(message = "가격은 양수여야 합니다")
        BigDecimal price,

        BigDecimal discountPrice,

        @NotNull(message = "재고 수량은 필수입니다")
        @Positive(message = "재고 수량은 양수여야 합니다")
        Integer stockQuantity,

        String mainImageUrl,

        List<String> additionalImageUrls,

        @NotNull(message = "카테고리 ID는 필수입니다")
        Long categoryId,

        String categoryName,

        Boolean isAvailable,

        LocalDateTime createdAt,

        LocalDateTime updatedAt,

        String brand,

        Double averageRating,

        Integer reviewCount
) {
    // 빌더 패턴을 위한 정적 메서드 (Record는 기본적으로 불변이므로 빌더가 유용)
    public static Builder builder() {
        return new Builder();
    }

    // 빌더 클래스
    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private BigDecimal discountPrice;
        private Integer stockQuantity;
        private String mainImageUrl;
        private List<String> additionalImageUrls;
        private Long categoryId;
        private String categoryName;
        private Boolean isAvailable;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String brand;
        private Double averageRating;
        private Integer reviewCount;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder price(BigDecimal price) { this.price = price; return this; }
        public Builder discountPrice(BigDecimal discountPrice) { this.discountPrice = discountPrice; return this; }
        public Builder stockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; return this; }
        public Builder mainImageUrl(String mainImageUrl) { this.mainImageUrl = mainImageUrl; return this; }
        public Builder additionalImageUrls(List<String> additionalImageUrls) { this.additionalImageUrls = additionalImageUrls; return this; }
        public Builder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public Builder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public Builder isAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder brand(String brand) { this.brand = brand; return this; }
        public Builder averageRating(Double averageRating) { this.averageRating = averageRating; return this; }
        public Builder reviewCount(Integer reviewCount) { this.reviewCount = reviewCount; return this; }

        public ProductDTO build() {
            return new ProductDTO(id, name, description, price, discountPrice, stockQuantity,
                    mainImageUrl, additionalImageUrls, categoryId, categoryName, isAvailable,
                    createdAt, updatedAt, brand, averageRating, reviewCount);
        }
    }
}