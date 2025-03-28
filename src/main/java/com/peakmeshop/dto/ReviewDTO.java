package com.peakmeshop.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 리뷰 정보를 전송하기 위한 DTO
 */
public record ReviewDTO(
        Long id,

        Long memberId,

        String memberName,

        @NotNull(message = "상품 ID는 필수입니다")
        Long productId,

        String productName,

        @NotNull(message = "평점은 필수입니다")
        @Min(value = 1, message = "평점은 최소 1점 이상이어야 합니다")
        @Max(value = 5, message = "평점은 최대 5점까지 가능합니다")
        Integer rating,

        @NotBlank(message = "내용은 필수입니다")
        @Size(min = 10, max = 1000, message = "내용은 10-1000자 사이여야 합니다")
        String content,

        String imageUrl,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {}