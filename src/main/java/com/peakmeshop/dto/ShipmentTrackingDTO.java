package com.peakmeshop.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentTrackingDTO {

    private Long id;
    private Long shipmentId;
    private String status;
    private LocalDateTime statusChangedAt;
    private String note;
    private String location;
}