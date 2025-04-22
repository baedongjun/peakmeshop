package com.peakmeshop.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.peakmeshop.api.dto.CartItemDTO;
import com.peakmeshop.api.dto.CouponDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Long id;
    private Long memberId;
    private String guestId;
    private List<CartItemDTO> items;
    private CouponDTO coupon;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal total;
    private Integer itemCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}