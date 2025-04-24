package com.peakmeshop.api.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductOptionDTO {

    private Long id;
    private Long productId;
    private String name;
    private String description;
    private String displayName;
    private Integer sortOrder;
    private boolean enabled;

    @Builder.Default
    private List<ProductOptionValueDTO> optionValues = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}