package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Cart;
import com.peakmeshop.api.dto.CartDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class, CartItemMapper.class, MemberMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface CartMapper {
    
    @Mapping(target = "memberId", source = "member.id")
    CartDTO toDTO(Cart cart);

    @Mapping(target = "member", ignore = true)
    Cart toEntity(CartDTO dto);
} 