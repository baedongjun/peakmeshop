package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.OrderDTO;
import com.peakmeshop.domain.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring", uses = {MemberMapper.class, OrderItemMapper.class, CouponMapper.class})
public interface OrderMapper {
    
    @Mapping(source = "member.id", target = "memberId")
    @Mapping(source = "coupon.id", target = "couponId")
    OrderDTO toDto(Order order);
    
    List<OrderDTO> toDtoList(List<Order> orders);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "memberId", target = "member.id")
    @Mapping(source = "couponId", target = "coupon.id")
    Order toEntity(OrderDTO orderDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "memberId", target = "member.id")
    @Mapping(source = "couponId", target = "coupon.id")
    void updateOrderFromDto(OrderDTO orderDTO, @MappingTarget Order order);
} 