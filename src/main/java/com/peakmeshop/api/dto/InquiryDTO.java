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
public class InquiryDTO {
    private Long id;
    private String userId;
    private String userName;
    private String title;
    private String content;
    private String type;
    private String status;
    private String answer;
    private LocalDateTime answeredAt;
    private Long productId;
    private String productName;
    private String productThumbnail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean isAnswered() {
        return answer != null && !answer.trim().isEmpty();
    }

    public String getStatusText() {
        if (isAnswered()) {
            return "답변완료";
        }
        return "답변대기";
    }

    public String getTypeText() {
        switch (type) {
            case "PRODUCT":
                return "상품문의";
            case "DELIVERY":
                return "배송문의";
            case "EXCHANGE":
                return "교환/반품문의";
            case "PAYMENT":
                return "결제문의";
            case "POINT":
                return "포인트문의";
            case "ETC":
                return "기타문의";
            default:
                return type;
        }
    }
} 