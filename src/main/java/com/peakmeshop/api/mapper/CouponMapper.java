package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Coupon;
import com.peakmeshop.api.dto.CouponDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface CouponMapper {
    
    CouponDTO toDTO(Coupon coupon);

    @Mapping(target = "memberCoupons", ignore = true)
    Coupon toEntity(CouponDTO dto);
} 