package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.CartDTO;
import com.peakmeshop.domain.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring", uses = {MemberMapper.class, CartItemMapper.class})
public interface CartMapper {
    
    @Mapping(source = "member.id", target = "memberId")
    CartDTO toDto(Cart cart);
    
    List<CartDTO> toDtoList(List<Cart> carts);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "memberId", target = "member.id")
    Cart toEntity(CartDTO cartDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "memberId", target = "member.id")
    void updateCartFromDto(CartDTO cartDTO, @MappingTarget Cart cart);
} 