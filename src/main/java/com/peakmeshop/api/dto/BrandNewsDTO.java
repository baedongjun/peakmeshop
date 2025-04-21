package com.peakmeshop.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandNewsDTO {
    private Long id;
    private Long brandId;
    private String title;
    private String content;
    private String thumbnail;
    private int viewCount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 