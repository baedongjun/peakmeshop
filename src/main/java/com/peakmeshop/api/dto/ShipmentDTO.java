package com.peakmeshop.api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.peakmeshop.api.dto.ShipmentTrackingDTO;
import com.peakmeshop.domain.enums.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentDTO {

    private Long id;
    private Long orderId;
    private String orderNumber;
    private String carrier;
    private String trackingNumber;
    private String shippingMethod;
    private ShipmentStatus status;
    private LocalDate estimatedDeliveryDate;
    private String shippingAddress;
    private String shippingCity;
    private String shippingState;
    private String shippingZipCode;
    private String shippingCountry;
    private String recipientName;
    private String recipientPhone;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ShipmentTrackingDTO> trackingHistory;
}