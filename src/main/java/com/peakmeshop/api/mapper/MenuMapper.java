package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Menu;
import com.peakmeshop.api.dto.MenuDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface MenuMapper {
    
    MenuDTO toDTO(Menu menu);

    Menu toEntity(MenuDTO dto);
} 