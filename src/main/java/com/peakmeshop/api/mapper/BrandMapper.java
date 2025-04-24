package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Brand;
import com.peakmeshop.api.dto.BrandDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface BrandMapper {
    
    @Mapping(target = "productCount", expression = "java(brand.getProducts().size())")
    BrandDTO toDTO(Brand brand);

    @Mapping(target = "products", ignore = true)
    Brand toEntity(BrandDTO dto);
} 