package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Category;
import com.peakmeshop.api.dto.CategoryDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface CategoryMapper {
    
    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "parentName", source = "parent.name")
    @Mapping(target = "childCount", expression = "java(category.getChildren().size())")
    @Mapping(target = "productCount", expression = "java(category.getProducts().size())")
    CategoryDTO toDTO(Category category);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "products", ignore = true)
    Category toEntity(CategoryDTO dto);
} 