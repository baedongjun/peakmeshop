package com.peakmeshop.api.dto;

import com.peakmeshop.api.dto.ProductDTO;

import java.time.LocalDateTime;

public record WishlistItemDTO(
        Long id,
        Long wishlistId,
        ProductDTO product,
        LocalDateTime addedAt,
        String notes,
        int priority,
        boolean isNotifyOnSale,
        boolean isNotifyOnStockAvailable
) {}