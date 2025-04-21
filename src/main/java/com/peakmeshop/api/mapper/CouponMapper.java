package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.CouponDTO;
import com.peakmeshop.domain.entity.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CouponMapper {
    
    CouponDTO toDto(Coupon coupon);
    
    List<CouponDTO> toDtoList(List<Coupon> coupons);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "usedCount", ignore = true)
    Coupon toEntity(CouponDTO couponDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "usedCount", ignore = true)
    void updateCouponFromDto(CouponDTO couponDTO, @MappingTarget Coupon coupon);
} 