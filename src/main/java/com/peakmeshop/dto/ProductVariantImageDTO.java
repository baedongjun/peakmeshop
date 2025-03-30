package com.peakmeshop.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantImageDTO {
    private Long id;
    private Long variantId;
    private Long fileId;
    private String url;
    private String altText;
    private Integer sortOrder;
    private boolean isMain;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}