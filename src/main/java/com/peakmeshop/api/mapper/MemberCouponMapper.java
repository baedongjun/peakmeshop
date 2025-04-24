package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.MemberCoupon;
import com.peakmeshop.api.dto.MemberCouponDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {CouponMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface MemberCouponMapper {
    
    @Mapping(target = "memberId", source = "member.id")
    @Mapping(target = "coupon", source = "coupon")
    MemberCouponDTO toDTO(MemberCoupon memberCoupon);

    @Mapping(target = "member", ignore = true)
    @Mapping(target = "coupon", ignore = true)
    MemberCoupon toEntity(MemberCouponDTO dto);
} 