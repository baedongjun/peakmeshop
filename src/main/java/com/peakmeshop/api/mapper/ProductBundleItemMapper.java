package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.ProductBundleItem;
import com.peakmeshop.api.dto.ProductBundleItemDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ProductBundleItemMapper {
    
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "bundleId", source = "bundle.id")
    ProductBundleItemDTO toDTO(ProductBundleItem productBundleItem);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "bundle", ignore = true)
    ProductBundleItem toEntity(ProductBundleItemDTO dto);
} 