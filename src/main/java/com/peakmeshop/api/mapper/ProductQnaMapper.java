package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.ProductQnaDTO;
import com.peakmeshop.domain.entity.ProductQna;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductQnaMapper {
    
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "member.id", target = "memberId")
    @Mapping(source = "member.name", target = "memberName")
    ProductQnaDTO toDTO(ProductQna entity);

    @Mapping(source = "productId", target = "product.id")
    @Mapping(source = "memberId", target = "member.id")
    ProductQna toEntity(ProductQnaDTO dto);
} 