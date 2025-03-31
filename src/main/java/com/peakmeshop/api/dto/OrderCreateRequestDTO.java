package com.peakmeshop.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 주문 생성 요청을 위한 DTO
 */
public record OrderCreateRequestDTO(
        @NotBlank(message = "배송 주소는 필수입니다")
        String shippingAddress,

        String shippingDetailAddress,

        @NotBlank(message = "우편번호는 필수입니다")
        String shippingZipCode,

        @NotBlank(message = "수령인 이름은 필수입니다")
        String recipientName,

        @NotBlank(message = "수령인 연락처는 필수입니다")
        String recipientPhone,

        @NotBlank(message = "결제 방법은 필수입니다")
        String paymentMethod,

        String impUid,  // 아임포트 결제 고유 ID (결제 모듈 사용 시)

        @NotEmpty(message = "주문 상품은 최소 1개 이상이어야 합니다")
        @Valid
        List<OrderItemCreateDTO> items
) {
}