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
    
    @Mapping(target = "productId", source = "product.id")
    ProductAttributeDTO toDTO(ProductAttribute attribute);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    ProductAttribute toEntity(ProductAttributeDTO dto);
} 