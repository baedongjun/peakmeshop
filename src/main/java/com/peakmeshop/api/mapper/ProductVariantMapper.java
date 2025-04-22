package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.ProductVariant;
import com.peakmeshop.api.dto.ProductVariantDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ProductVariantMapper {
    
    @Mapping(target = "productId", source = "product.id")
    ProductVariantDTO toDTO(ProductVariant productVariant);

    @Mapping(target = "product", ignore = true)
    ProductVariant toEntity(ProductVariantDTO dto);
} 