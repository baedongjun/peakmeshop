package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Review;
import com.peakmeshop.api.dto.ReviewDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ReviewMapper {
    
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "memberId", source = "member.id")
    @Mapping(target = "memberName", source = "member.name")
    ReviewDTO toDTO(Review review);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "member", ignore = true)
    @Mapping(target = "reviewImages", ignore = true)
    Review toEntity(ReviewDTO dto);
} 