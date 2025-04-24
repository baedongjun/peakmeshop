package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Cart;
import com.peakmeshop.api.dto.CartDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class, CartItemMapper.class, MemberMapper.class, CouponMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface CartMapper {
    
    @Mapping(target = "memberId", source = "member.id")
    @Mapping(target = "cartItems", source = "cartItems")
    @Mapping(target = "coupon", source = "coupon")
    CartDTO toDTO(Cart cart);

    @Mapping(target = "member", ignore = true)
    @Mapping(target = "coupon", ignore = true)
    Cart toEntity(CartDTO dto);
} 