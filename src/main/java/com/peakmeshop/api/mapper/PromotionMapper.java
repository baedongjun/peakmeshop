package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Promotion;
import com.peakmeshop.api.dto.PromotionDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class, CategoryMapper.class, ProductMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface PromotionMapper {
    
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "productId", source = "product.id")
    PromotionDTO toDTO(Promotion promotion);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "product", ignore = true)
    Promotion toEntity(PromotionDTO dto);
} 