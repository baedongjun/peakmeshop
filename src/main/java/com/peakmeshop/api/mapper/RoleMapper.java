package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Role;
import com.peakmeshop.api.dto.RoleDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface RoleMapper {
    
    RoleDTO toDTO(Role role);

    Role toEntity(RoleDTO dto);
} 