package com.peakmeshop.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDTO {
    private Long id;
    private Long productId;
    private String url;
    private String alt;
    private Integer sortOrder;
    private Boolean isMain;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}