package com.peakmeshop.dto;

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
public class ProductComparisonDTO {

    private Long id;
    private Long memberId;
    private List<ProductDTO> products;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}