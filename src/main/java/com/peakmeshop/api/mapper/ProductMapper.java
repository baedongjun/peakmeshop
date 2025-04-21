package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.ProductDTO;
import com.peakmeshop.domain.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    @Mapping(target = "categoryName", source = "category.name")
    ProductDTO toDto(Product product);
    
    List<ProductDTO> toDtoList(List<Product> products);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductDTO productDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateProductFromDto(ProductDTO productDTO, @MappingTarget Product product);
} 