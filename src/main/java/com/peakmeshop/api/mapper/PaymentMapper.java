package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.PaymentDTO;
import com.peakmeshop.domain.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "member.id", target = "memberId")
    PaymentDTO toDto(Payment payment);
    
    List<PaymentDTO> toDtoList(List<Payment> payments);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "paymentKey", ignore = true)
    @Mapping(source = "orderId", target = "order.id")
    @Mapping(source = "memberId", target = "member.id")
    Payment toEntity(PaymentDTO paymentDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "paymentKey", ignore = true)
    @Mapping(source = "orderId", target = "order.id")
    @Mapping(source = "memberId", target = "member.id")
    void updatePaymentFromDto(PaymentDTO paymentDTO, @MappingTarget Payment payment);
} 