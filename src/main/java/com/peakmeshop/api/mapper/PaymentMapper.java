package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Payment;
import com.peakmeshop.api.dto.PaymentDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class, OrderMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface PaymentMapper {
    
    @Mapping(target = "orderId", source = "order.id")
    PaymentDTO toDTO(Payment payment);

    @Mapping(target = "order", ignore = true)
    Payment toEntity(PaymentDTO dto);
} 