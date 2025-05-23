package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.ProductOptionValueDTO;
import com.peakmeshop.domain.entity.ProductOptionValue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ProductOptionValueMapper {
    
    @Mapping(target = "optionId", source = "option.id")
    @Mapping(target = "name", ignore = true)
    ProductOptionValueDTO toDTO(ProductOptionValue optionValue);
    
    @Mapping(target = "option", ignore = true)
    ProductOptionValue toEntity(ProductOptionValueDTO dto);
} 