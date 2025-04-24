package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.CartItem;
import com.peakmeshop.api.dto.CartItemDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class, ProductMapper.class, ProductOptionMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface CartItemMapper {
    
    @Mapping(target = "cartId", source = "cart.id")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "optionId", source = "option.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "optionName", source = "option.name")
    CartItemDTO toDTO(CartItem cartItem);

    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "option", ignore = true)
    CartItem toEntity(CartItemDTO dto);
} 