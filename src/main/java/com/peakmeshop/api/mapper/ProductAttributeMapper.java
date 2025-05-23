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
    @Mapping(target = "attributeOptionId", source = "attributeOption.id")
    ProductAttributeDTO toDTO(ProductAttribute entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "attributeOption", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductAttribute toEntity(ProductAttributeDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "attributeOption", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(ProductAttributeDTO dto, @MappingTarget ProductAttribute entity);
} 