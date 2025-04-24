package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Payment;
import com.peakmeshop.api.dto.PaymentDTO;
import org.mapstruct.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface PaymentMapper {
    
    @Mapping(target = "orderId", source = "order.id")
    PaymentDTO toDTO(Payment payment);

    @Mapping(target = "order", ignore = true)
    Payment toEntity(PaymentDTO dto);

    @Named("fromMap")
    default PaymentDTO fromMap(Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        return PaymentDTO.builder()
                .paymentMethod((String) map.get("paymentMethod"))
                .paymentKey((String) map.get("paymentKey"))
                .orderId((String) map.get("orderId"))
                .amount(new BigDecimal(String.valueOf(map.get("amount"))))
                .status((String) map.get("status"))
                .description((String) map.get("description"))
                .paidAt(map.get("paidAt") != null ? LocalDateTime.parse((String) map.get("paidAt")) : null)
                .cancelledAt(map.get("cancelledAt") != null ? LocalDateTime.parse((String) map.get("cancelledAt")) : null)
                .failureReason((String) map.get("failureReason"))
                .additionalInfo(map)
                .build();
    }
} 