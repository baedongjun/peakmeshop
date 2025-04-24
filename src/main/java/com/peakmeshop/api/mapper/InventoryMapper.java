package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.InventoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import com.peakmeshop.domain.entity.Inventory;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class, ProductMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface InventoryMapper {
    
    InventoryMapper INSTANCE = Mappers.getMapper(InventoryMapper.class);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    InventoryDTO toDTO(Inventory inventory);

    @Mapping(target = "product", ignore = true)
    Inventory toEntity(InventoryDTO dto);
} 