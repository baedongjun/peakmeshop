package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.ShipmentTracking;
import com.peakmeshop.api.dto.ShipmentTrackingDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class, ShipmentMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ShipmentTrackingMapper {
    
    @Mapping(target = "shipmentId", source = "shipment.id")
    ShipmentTrackingDTO toDTO(ShipmentTracking shipmentTracking);

    @Mapping(target = "shipment", ignore = true)
    ShipmentTracking toEntity(ShipmentTrackingDTO dto);
} 