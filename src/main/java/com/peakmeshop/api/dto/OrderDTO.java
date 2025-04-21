package com.peakmeshop.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.peakmeshop.domain.enums.OrderStatus;
import com.peakmeshop.domain.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;
    private String orderNumber;
    private String userId;
    private String userName;
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private BigDecimal finalAmount;
    private Integer usedPoints;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime paidAt;
    
    // 배송 정보
    private String recipientName;
    private String recipientPhone;
    private String zipCode;
    private String address;
    private String addressDetail;
    private String deliveryMessage;
    private String deliveryCompany;
    private String deliveryTracking;
    private String deliveryStatus;
    private LocalDateTime deliveredAt;
    
    // 주문 상품 목록
    private List<OrderItemDTO> orderItems;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getDeliveryTracking() {
        return deliveryTracking;
    }

    public String getUserId() {
        return userId;
    }

    public String getStatusText() {
        switch (status) {
            case "PENDING":
                return "주문접수";
            case "PAID":
                return "결제완료";
            case "PREPARING":
                return "상품준비중";
            case "SHIPPING":
                return "배송중";
            case "DELIVERED":
                return "배송완료";
            case "COMPLETED":
                return "구매확정";
            case "CANCELLED":
                return "주문취소";
            case "REFUNDING":
                return "환불진행중";
            case "REFUNDED":
                return "환불완료";
            default:
                return status;
        }
    }

    public String getDeliveryStatusText() {
        switch (deliveryStatus) {
            case "PREPARING":
                return "배송준비중";
            case "SHIPPING":
                return "배송중";
            case "DELIVERED":
                return "배송완료";
            default:
                return deliveryStatus;
        }
    }

    public boolean canCancel() {
        return "PENDING".equals(status) || "PAID".equals(status);
    }

    public boolean canRefund() {
        return "DELIVERED".equals(status) && 
               deliveredAt != null && 
               deliveredAt.plusDays(7).isAfter(LocalDateTime.now());
    }

    public boolean canReview() {
        return "COMPLETED".equals(status);
    }

    public boolean isDeliveryCompleted() {
        return "DELIVERED".equals(deliveryStatus) || "COMPLETED".equals(status);
    }
}