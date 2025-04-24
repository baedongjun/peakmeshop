package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Shipment;
import com.peakmeshop.api.dto.ShipmentDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ShipmentMapper {
    
    @Mapping(target = "orderId", source = "order.id")
    ShipmentDTO toDTO(Shipment shipment);

    @Mapping(target = "order", ignore = true)
    Shipment toEntity(ShipmentDTO dto);
} 