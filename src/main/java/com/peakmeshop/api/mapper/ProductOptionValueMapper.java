package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.ProductOptionValue;
import com.peakmeshop.api.dto.ProductOptionValueDTO;
import org.mapstruct.*;
import java.math.BigDecimal;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ProductOptionValueMapper {
    
    @Mapping(target = "optionId", source = "option.id")
    @Mapping(target = "value", source = "value")
    @Mapping(target = "additionalPrice", source = "additionalPrice")
    @Mapping(target = "stock", source = "stock")
    @Mapping(target = "sku", source = "sku")
    ProductOptionValueDTO toDTO(ProductOptionValue value);

    @Mapping(target = "option", ignore = true)
    ProductOptionValue toEntity(ProductOptionValueDTO dto);

    @AfterMapping
    default void afterToEntity(@MappingTarget ProductOptionValue target, ProductOptionValueDTO source) {
        if (target.getAdditionalPrice() == null) {
            target.setAdditionalPrice(BigDecimal.ZERO);
        }
    }
} 