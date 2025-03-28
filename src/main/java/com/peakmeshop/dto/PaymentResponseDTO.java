package com.peakmeshop.dto;

import java.math.BigDecimal;

/**
 * 결제 응답을 위한 DTO
 */
public record PaymentResponseDTO(
        String impUid,
        String merchantUid,
        BigDecimal amount,
        String status
) {}