package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Order;
import com.peakmeshop.api.dto.OrderDTO;
import com.peakmeshop.api.dto.OrderSummaryDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class, OrderItemMapper.class, MemberMapper.class, ShippingMapper.class, PaymentMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface OrderMapper {
    
    @Mapping(target = "memberId", source = "member.id")
    @Mapping(target = "memberName", source = "member.name")
    @Mapping(target = "paymentId", source = "payment.id")
    @Mapping(target = "shippingId", source = "shipping.id")
    @Mapping(target = "paymentMethodString", source = "paymentMethod")
    @Mapping(target = "statusString", source = "status")
    @Mapping(target = "shippingAddress", source = "shipping.address")
    @Mapping(target = "billingAddress", source = "payment.address")
    @Mapping(target = "paymentStatus", source = "payment.status")
    @Mapping(target = "shippingStatus", source = "shipping.status")
    @Mapping(target = "items", source = "items")
    OrderDTO toDTO(Order order);

    @Mapping(target = "member", ignore = true)
    @Mapping(target = "payment", ignore = true)
    @Mapping(target = "shipping", ignore = true)
    @Mapping(target = "items", ignore = true)
    Order toEntity(OrderDTO dto);

    @Mapping(target = "memberName", source = "member.name")
    @Mapping(target = "totalItems", expression = "java(order.getItems().size())")
    OrderSummaryDTO toSummaryDTO(Order order);

    @AfterMapping
    default void afterToEntity(@MappingTarget Order target, OrderDTO source) {
        if (target.getItems() != null) {
            target.getItems().forEach(item -> item.setOrder(target));
        }
    }
} 