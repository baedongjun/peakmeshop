package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.StockNotification;
import com.peakmeshop.api.dto.StockNotificationDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class, MemberMapper.class, ProductMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface StockNotificationMapper {
    
    @Mapping(target = "memberId", source = "member.id")
    @Mapping(target = "memberName", source = "member.name")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    StockNotificationDTO toDTO(StockNotification notification);

    @Mapping(target = "member", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    StockNotification toEntity(StockNotificationDTO dto);
} 