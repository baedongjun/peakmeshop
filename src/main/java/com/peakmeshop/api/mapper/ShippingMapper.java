package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Shipping;
import com.peakmeshop.api.dto.ShippingDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ShippingMapper {
    
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "orderNumber", source = "order.orderNumber")
    ShippingDTO toDTO(Shipping shipping);

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    Shipping toEntity(ShippingDTO dto);
} 