package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.ProductReviewDTO;
import com.peakmeshop.domain.entity.ProductReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductReviewMapper {
    
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "member.id", target = "memberId")
    @Mapping(source = "member.name", target = "memberName")
    ProductReviewDTO toDTO(ProductReview entity);

    @Mapping(source = "productId", target = "product.id")
    @Mapping(source = "memberId", target = "member.id")
    ProductReview toEntity(ProductReviewDTO dto);
} 