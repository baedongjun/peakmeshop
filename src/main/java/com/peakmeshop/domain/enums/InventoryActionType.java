package com.peakmeshop.domain.enums;

public enum InventoryActionType {
    INCREASE,    // 재고 증가
    DECREASE,    // 재고 감소
    RESERVE,     // 재고 예약 (주문 시)
    RELEASE,     // 예약 해제 (주문 취소 시)
    CONFIRM      // 주문 확정
} 