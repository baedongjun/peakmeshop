package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.OrderItemDTO;
import com.peakmeshop.domain.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface OrderItemMapper {
    
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "order.id", target = "orderId")
    OrderItemDTO toDto(OrderItem orderItem);
    
    List<OrderItemDTO> toDtoList(List<OrderItem> orderItems);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "productId", target = "product.id")
    @Mapping(source = "orderId", target = "order.id")
    OrderItem toEntity(OrderItemDTO orderItemDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "productId", target = "product.id")
    @Mapping(source = "orderId", target = "order.id")
    void updateOrderItemFromDto(OrderItemDTO orderItemDTO, @MappingTarget OrderItem orderItem);
} 