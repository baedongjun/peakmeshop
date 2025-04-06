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
public class ProductQnaDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Long memberId;
    private String memberName;
    private String title;
    private String content;
    private String answer;
    private String status;
    private Boolean isSecret;
    private LocalDateTime answeredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 