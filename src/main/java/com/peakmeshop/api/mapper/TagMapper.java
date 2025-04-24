package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Tag;
import com.peakmeshop.api.dto.TagDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface TagMapper {
    
    TagDTO toDTO(Tag tag);

    Tag toEntity(TagDTO dto);

    @AfterMapping
    default void afterToDTO(@MappingTarget TagDTO target, Tag source) {
        if (source.getProducts() != null) {
            target.setProductCount(source.getProducts().size());
        }
    }
} 