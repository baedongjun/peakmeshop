package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.OrderItem;
import com.peakmeshop.api.dto.OrderItemDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class, ProductMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface OrderItemMapper {
    
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "productImage", source = "product.mainImage")
    OrderItemDTO toDTO(OrderItem orderItem);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItem toEntity(OrderItemDTO dto);
} 