package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Product;
import com.peakmeshop.api.dto.ProductDTO;
import com.peakmeshop.api.dto.ProductSummaryDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class, CategoryMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ProductMapper {
    
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    ProductDTO toDTO(Product product);

    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductDTO productDTO);

    @Mapping(target = "categoryName", source = "category.name")
    ProductSummaryDTO toSummaryDTO(Product product);

    @AfterMapping
    default void afterToDTO(@MappingTarget ProductDTO target, Product source) {
        if (source.getImages() != null && !source.getImages().isEmpty()) {
            target.setMainImage(source.getImages().get(0).getUrl());
        }
    }
} 