package com.peakmeshop.util;

public class AppConstants {

    // 페이지네이션 기본값
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";

    // 주문 상태
    public static final String ORDER_STATUS_PENDING = "PENDING";
    public static final String ORDER_STATUS_PAID = "PAID";
    public static final String ORDER_STATUS_PROCESSING = "PROCESSING";
    public static final String ORDER_STATUS_SHIPPED = "SHIPPED";
    public static final String ORDER_STATUS_DELIVERED = "DELIVERED";
    public static final String ORDER_STATUS_COMPLETED = "COMPLETED";
    public static final String ORDER_STATUS_CANCELLED = "CANCELLED";
    public static final String ORDER_STATUS_REFUNDED = "REFUNDED";

    // 배송 상태
    public static final String SHIPPING_STATUS_PREPARING = "PREPARING";
    public static final String SHIPPING_STATUS_SHIPPED = "SHIPPED";
    public static final String SHIPPING_STATUS_IN_TRANSIT = "IN_TRANSIT";
    public static final String SHIPPING_STATUS_DELIVERED = "DELIVERED";

    // 결제 상태
    public static final String PAYMENT_STATUS_PENDING = "PENDING";
    public static final String PAYMENT_STATUS_COMPLETED = "COMPLETED";
    public static final String PAYMENT_STATUS_FAILED = "FAILED";
    public static final String PAYMENT_STATUS_REFUNDED = "REFUNDED";

    // 할인 유형
    public static final String DISCOUNT_TYPE_PERCENTAGE = "PERCENTAGE";
    public static final String DISCOUNT_TYPE_FIXED = "FIXED";

    // 회원 역할
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";

    // 알림 유형
    public static final String NOTIFICATION_TYPE_SYSTEM = "SYSTEM";
    public static final String NOTIFICATION_TYPE_ORDER = "ORDER";
    public static final String NOTIFICATION_TYPE_PROMOTION = "PROMOTION";

    // Q&A 상태
    public static final String QNA_STATUS_PENDING = "PENDING";
    public static final String QNA_STATUS_ANSWERED = "ANSWERED";

    // 포인트 유형
    public static final String POINT_TYPE_EARN = "EARN";
    public static final String POINT_TYPE_USE = "USE";

    private AppConstants() {
        // 유틸리티 클래스이므로 인스턴스화 방지
    }
}