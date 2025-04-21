package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.InventoryDTO;
import com.peakmeshop.domain.entity.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface InventoryMapper {
    
    InventoryDTO toDto(Inventory inventory);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Inventory toEntity(InventoryDTO inventoryDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateInventoryFromDto(InventoryDTO inventoryDTO, @MappingTarget Inventory inventory);
} 