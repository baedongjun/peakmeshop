package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.InventoryHistoryDTO;
import com.peakmeshop.domain.entity.InventoryHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface InventoryHistoryMapper {
    
    InventoryHistoryDTO toDto(InventoryHistory inventoryHistory);
    
    InventoryHistory toEntity(InventoryHistoryDTO inventoryHistoryDTO);
    
}