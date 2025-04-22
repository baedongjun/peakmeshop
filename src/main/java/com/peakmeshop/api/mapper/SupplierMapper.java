package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Supplier;
import com.peakmeshop.api.dto.SupplierDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface SupplierMapper {
    
    SupplierDTO toDTO(Supplier supplier);

    Supplier toEntity(SupplierDTO dto);
} 