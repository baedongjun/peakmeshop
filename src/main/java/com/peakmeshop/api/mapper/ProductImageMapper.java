package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.ProductImage;
import com.peakmeshop.api.dto.ProductImageDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ProductImageMapper {
    
    @Mapping(target = "productId", source = "product.id")
    ProductImageDTO toDTO(ProductImage productImage);

    @Mapping(target = "product", ignore = true)
    ProductImage toEntity(ProductImageDTO dto);
} 