package com.peakmeshop.api.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CancellationDTO {
    private Long id;
    private String orderNumber;
    private Long memberId;
    private String memberName;
    private Double totalAmount;
    private String reason;
    private LocalDateTime cancelledAt;
} 