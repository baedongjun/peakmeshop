package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.WishlistDTO;
import com.peakmeshop.domain.entity.Wishlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring", uses = {MemberMapper.class, ProductMapper.class})
public interface WishlistMapper {
    
    @Mapping(target = "memberId", source = "member.id")
    WishlistDTO toDto(Wishlist wishlist);
    
    @Mapping(target = "member", ignore = true)
    Wishlist toEntity(WishlistDTO wishlistDTO);
}