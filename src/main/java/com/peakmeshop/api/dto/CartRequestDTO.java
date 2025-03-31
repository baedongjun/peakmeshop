package com.peakmeshop.api.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartRequestDTO {

    private Long productId;
    private Long variantId;
    private int quantity;
    private List<CartOptionDTO> options;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartOptionDTO {
        private Long optionId;
        private String value;
    }
}