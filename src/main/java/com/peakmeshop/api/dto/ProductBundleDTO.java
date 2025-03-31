package com.peakmeshop.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.peakmeshop.api.dto.ProductBundleItemDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBundleDTO {

    private Long id;
    private String name;
    private String description;
    private BigDecimal discountRate;
    private BigDecimal discountAmount;
    private boolean active;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private List<ProductBundleItemDTO> items = new ArrayList<>();

    // 총 정가 계산
    public BigDecimal getTotalOriginalPrice() {
        return items.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // 총 할인가 계산
    public BigDecimal getTotalDiscountedPrice() {
        BigDecimal totalOriginalPrice = getTotalOriginalPrice();

        if (discountRate != null) {
            BigDecimal discountMultiplier = BigDecimal.ONE.subtract(discountRate.divide(new BigDecimal(100)));
            return totalOriginalPrice.multiply(discountMultiplier);
        } else if (discountAmount != null) {
            return totalOriginalPrice.subtract(discountAmount);
        }

        return totalOriginalPrice;
    }

    // 총 할인액 계산
    public BigDecimal getTotalDiscountAmount() {
        return getTotalOriginalPrice().subtract(getTotalDiscountedPrice());
    }
}