package com.peakmeshop.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBundleItemDTO {

    private Long id;
    private Long bundleId;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private int quantity;
}