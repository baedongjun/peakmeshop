package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.ProductOption;
import com.peakmeshop.api.dto.ProductOptionDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class, ProductMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ProductOptionMapper {
    
    @Mapping(target = "productId", source = "product.id")
    ProductOptionDTO toDTO(ProductOption productOption);

    @Mapping(target = "product", ignore = true)
    ProductOption toEntity(ProductOptionDTO dto);
} 