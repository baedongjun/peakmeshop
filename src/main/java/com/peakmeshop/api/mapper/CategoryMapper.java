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
    CategoryDTO toDTO(Category category);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "products", ignore = true)
    Category toEntity(CategoryDTO dto);
} 