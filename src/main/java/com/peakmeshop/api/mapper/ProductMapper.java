package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Product;
import com.peakmeshop.api.dto.ProductDTO;
import com.peakmeshop.api.dto.ProductSummaryDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class, CategoryMapper.class, BrandMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ProductMapper {
    
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "brandId", source = "brand.id")
    @Mapping(target = "brandName", source = "brand.name")
    @Mapping(target = "inventoryId", source = "inventory.id")
    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "supplierName", source = "supplier.name")
    @Mapping(target = "mainImage", source = "mainImage")
    ProductDTO toDTO(Product product);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "inventory", ignore = true)
    Product toEntity(ProductDTO dto);

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "brandName", source = "brand.name")
    ProductSummaryDTO toSummaryDTO(Product product);

    @AfterMapping
    default void afterToDTO(@MappingTarget ProductDTO target, Product source) {
        if (source.getImages() != null && !source.getImages().isEmpty()) {
            target.setMainImage(source.getMainImage());
        }
    }
} 