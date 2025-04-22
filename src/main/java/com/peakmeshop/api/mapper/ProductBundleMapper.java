package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.ProductBundle;
import com.peakmeshop.api.dto.ProductBundleDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class, ProductBundleItemMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ProductBundleMapper {
    
    ProductBundleDTO toDTO(ProductBundle productBundle);

    @Mapping(target = "items", ignore = true)
    ProductBundle toEntity(ProductBundleDTO dto);
} 