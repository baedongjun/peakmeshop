package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.WishlistDTO;
import com.peakmeshop.domain.entity.Wishlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring", uses = {MemberMapper.class, ProductMapper.class})
public interface WishlistMapper {
    
    @Mapping(source = "member.id", target = "memberId")
    @Mapping(source = "product.id", target = "productId")
    WishlistDTO toDto(Wishlist wishlist);
    
    List<WishlistDTO> toDtoList(List<Wishlist> wishlists);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "memberId", target = "member.id")
    @Mapping(source = "productId", target = "product.id")
    Wishlist toEntity(WishlistDTO wishlistDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "memberId", target = "member.id")
    @Mapping(source = "productId", target = "product.id")
    void updateWishlistFromDto(WishlistDTO wishlistDTO, @MappingTarget Wishlist wishlist);
} 