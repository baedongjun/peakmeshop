package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.ProductBundle;
import com.peakmeshop.api.dto.ProductBundleDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class, ProductMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ProductBundleMapper {
    
    ProductBundleDTO toDTO(ProductBundle productBundle);

    ProductBundle toEntity(ProductBundleDTO dto);
} 