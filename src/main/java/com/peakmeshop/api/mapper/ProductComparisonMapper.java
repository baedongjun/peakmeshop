package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.ProductComparison;
import com.peakmeshop.api.dto.ProductComparisonDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class, ProductMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ProductComparisonMapper {
    
    @Mapping(target = "memberId", source = "member.id")
    @Mapping(target = "productIds", expression = "java(comparison.getProducts())")
    ProductComparisonDTO toDTO(ProductComparison comparison);

    @Mapping(target = "member", ignore = true)
    @Mapping(target = "products", ignore = true)
    ProductComparison toEntity(ProductComparisonDTO dto);
} 