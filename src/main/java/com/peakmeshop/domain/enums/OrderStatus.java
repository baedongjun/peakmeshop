package com.peakmeshop.domain.enums;

public enum OrderStatus {
    PENDING("주문 대기"),
    PAID("결제 완료"),
    PROCESSING("처리 중"),
    PREPARING("상품 준비중"),
    SHIPPING("배송 중"),
    SHIPPED("배송 시작"),
    DELIVERED("배송 완료"),
    COMPLETED("주문 완료"),
    CANCELLED("주문 취소"),
    REFUNDED("환불 완료"),
    PARTIALLY_REFUNDED("부분 환불");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}