package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.AdminMenu;
import com.peakmeshop.api.dto.AdminMenuDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface AdminMenuMapper {
    
    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "parentName", source = "parent.name")
    AdminMenuDTO toDTO(AdminMenu menu);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    AdminMenu toEntity(AdminMenuDTO dto);
} 