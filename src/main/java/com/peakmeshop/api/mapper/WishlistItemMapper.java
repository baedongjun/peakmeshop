package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.WishlistItemDTO;
import com.peakmeshop.domain.entity.WishlistItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface WishlistItemMapper {
    
    WishlistItemDTO toDto(WishlistItem wishlistItem);
    
    WishlistItem toEntity(WishlistItemDTO wishlistItemDTO);
}