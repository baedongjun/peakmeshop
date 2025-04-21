package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.WishlistDTO;
import com.peakmeshop.domain.entity.Wishlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {MemberMapper.class, WishlistItemMapper.class})
public interface WishlistMapper {
    
    WishlistDTO toDto(Wishlist wishlist);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Wishlist toEntity(WishlistDTO wishlistDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateWishlistFromDto(WishlistDTO wishlistDTO, @MappingTarget Wishlist wishlist);
} 