package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.ProductAttribute;
import com.peakmeshop.api.dto.ProductAttributeDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ProductAttributeMapper {
    
    ProductAttributeDTO toDTO(ProductAttribute productAttribute);

    ProductAttribute toEntity(ProductAttributeDTO dto);
} 