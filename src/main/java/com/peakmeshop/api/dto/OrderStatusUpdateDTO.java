package com.peakmeshop.api.dto;

import com.peakmeshop.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusUpdateDTO {

        private OrderStatus status;
        private String transactionId;
        private String trackingNumber;
        private String shippingCompany;
        private String reason;

        // 호환성을 위한 메서드
        public static class OrderStatusUpdateDTOBuilder {
                public OrderStatusUpdateDTOBuilder cancelReason(String reason) {
                        this.reason = reason;
                        return this;
                }
        }
}