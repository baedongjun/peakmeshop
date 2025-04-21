package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.CategoryDTO;
import com.peakmeshop.domain.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    
    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "parent.name", target = "parentName")
    CategoryDTO toDto(Category category);
    
    List<CategoryDTO> toDtoList(List<Category> categories);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(source = "parentId", target = "parent.id")
    Category toEntity(CategoryDTO categoryDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(source = "parentId", target = "parent.id")
    void updateCategoryFromDto(CategoryDTO categoryDTO, @MappingTarget Category category);
} 