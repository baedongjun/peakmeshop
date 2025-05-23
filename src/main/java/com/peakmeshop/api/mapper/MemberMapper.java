package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Member;
import com.peakmeshop.api.dto.MemberDTO;
import com.peakmeshop.api.dto.MemberSummaryDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class, AddressMapper.class, CouponMapper.class, PointMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface MemberMapper {
    
    @Mapping(target = "gradeId", source = "memberGrade.id")
    @Mapping(target = "gradeName", source = "memberGrade.name")
    @Mapping(target = "currentPoint", source = "point.currentPoint")
    @Mapping(target = "totalOrders", expression = "java(member.getOrders().size())")
    @Mapping(target = "totalReviews", expression = "java(member.getReviews().size())")
    MemberDTO toDTO(Member member);

    @Mapping(target = "memberGrade", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "wishlist", ignore = true)
    @Mapping(target = "point", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    Member toEntity(MemberDTO memberDTO);
}