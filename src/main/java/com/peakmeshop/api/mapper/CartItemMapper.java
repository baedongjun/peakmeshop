package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.CartItemDTO;
import com.peakmeshop.domain.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface CartItemMapper {
    
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "cart.id", target = "cartId")
    CartItemDTO toDto(CartItem cartItem);
    
    List<CartItemDTO> toDtoList(List<CartItem> cartItems);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "productId", target = "product.id")
    @Mapping(source = "cartId", target = "cart.id")
    CartItem toEntity(CartItemDTO cartItemDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "productId", target = "product.id")
    @Mapping(source = "cartId", target = "cart.id")
    void updateCartItemFromDto(CartItemDTO cartItemDTO, @MappingTarget CartItem cartItem);
} 