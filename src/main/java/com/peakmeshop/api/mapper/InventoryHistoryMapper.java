package com.peakmeshop.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.peakmeshop.domain.entity.InventoryHistory;
import com.peakmeshop.api.dto.InventoryHistoryDTO;

@Mapper(componentModel = "spring")
public interface InventoryHistoryMapper {
    
    InventoryHistoryMapper INSTANCE = Mappers.getMapper(InventoryHistoryMapper.class);

    @Mapping(target = "productId", source = "inventory.product.id")
    @Mapping(target = "productName", source = "inventory.product.name")
    @Mapping(target = "memberId", source = "member.id")
    @Mapping(target = "memberName", source = "member.name")
    InventoryHistoryDTO toDTO(InventoryHistory entity);

    @Mapping(target = "inventory", ignore = true)
    @Mapping(target = "member", ignore = true)
    InventoryHistory toEntity(InventoryHistoryDTO dto);
}