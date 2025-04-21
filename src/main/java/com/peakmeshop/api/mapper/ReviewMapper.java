package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.ReviewDTO;
import com.peakmeshop.domain.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring", uses = {MemberMapper.class, ProductMapper.class})
public interface ReviewMapper {
    
    @Mapping(source = "member.id", target = "memberId")
    @Mapping(source = "product.id", target = "productId")
    ReviewDTO toDto(Review review);
    
    List<ReviewDTO> toDtoList(List<Review> reviews);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "memberId", target = "member.id")
    @Mapping(source = "productId", target = "product.id")
    Review toEntity(ReviewDTO reviewDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "memberId", target = "member.id")
    @Mapping(source = "productId", target = "product.id")
    void updateReviewFromDto(ReviewDTO reviewDTO, @MappingTarget Review review);
} 