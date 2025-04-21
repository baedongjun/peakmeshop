package com.peakmeshop.api.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

    private Long id;

    @NotNull(message = "상품 ID는 필수 입력 항목입니다")
    private Long productId;

    private String productName;

    @NotNull(message = "회원 ID는 필수 입력 항목입니다")
    private Long memberId;

    private String memberName;

    @NotNull(message = "평점은 필수 입력 항목입니다")
    @Min(value = 1, message = "평점은 1점 이상이어야 합니다")
    @Max(value = 5, message = "평점은 5점 이하이어야 합니다")
    private Integer rating;

    @NotBlank(message = "제목은 필수 입력 항목입니다")
    @Size(max = 100, message = "제목은 100자 이하로 입력해주세요")
    private String title;

    @NotBlank(message = "내용은 필수 입력 항목입니다")
    @Size(max = 1000, message = "내용은 1000자 이하로 입력해주세요")
    private String content;

    private boolean recommended;

    private Integer helpfulCount;

    private boolean adminReplied;

    private String adminReply;

    private LocalDateTime adminReplyDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String imageUrl;

    private Boolean isVerifiedPurchase;
}