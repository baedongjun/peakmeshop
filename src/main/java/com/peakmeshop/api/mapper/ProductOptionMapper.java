package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.ProductOption;
import com.peakmeshop.api.dto.ProductOptionDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class, ProductMapper.class, ProductOptionValueMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ProductOptionMapper {
    
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "optionValues", source = "optionValues")
    ProductOptionDTO toDTO(ProductOption productOption);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "optionValues", ignore = true)
    ProductOption toEntity(ProductOptionDTO dto);

    @AfterMapping
    default void afterToEntity(@MappingTarget ProductOption target, ProductOptionDTO source) {
        if (target.getOptionValues() != null) {
            target.getOptionValues().forEach(value -> value.setOption(target));
        }
    }
} 