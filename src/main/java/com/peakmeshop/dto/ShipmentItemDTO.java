package com.peakmeshop.dto;

public record ShipmentItemDTO(
        Long id,
        Long shipmentId,
        Long orderItemId,
        Long productId,
        String productName,
        String sku,
        int quantity
) {}