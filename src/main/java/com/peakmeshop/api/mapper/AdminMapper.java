package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Admin;
import com.peakmeshop.api.dto.AdminDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface AdminMapper {
    
    AdminDTO toDTO(Admin admin);

    Admin toEntity(AdminDTO dto);
} 