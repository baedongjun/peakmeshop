package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Order;
import com.peakmeshop.api.dto.OrderDTO;
import com.peakmeshop.api.dto.OrderSummaryDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class, OrderItemMapper.class, MemberMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface OrderMapper {
    
    @Mapping(target = "memberId", source = "member.id")
    @Mapping(target = "memberName", source = "member.name")
    @Mapping(target = "paymentId", source = "payment.id")
    @Mapping(target = "shippingId", source = "shipping.id")
    @Mapping(target = "shippingAddress", source = "shipping.shippingAddress")
    OrderDTO toDTO(Order order);

    @Mapping(target = "member", ignore = true)
    @Mapping(target = "payment", ignore = true)
    @Mapping(target = "shipping", ignore = true)
    Order toEntity(OrderDTO dto);

    @Mapping(target = "memberName", source = "member.name")
    OrderSummaryDTO toSummaryDTO(Order order);
} 