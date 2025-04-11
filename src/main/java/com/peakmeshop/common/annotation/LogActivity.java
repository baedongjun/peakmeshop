package com.peakmeshop.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 활동 로그를 기록할 메서드에 표시하는 어노테이션
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogActivity {
    /**
     * 활동 유형 (예: LOGIN, LOGOUT, CREATE, UPDATE, DELETE)
     */
    String type() default "";

    /**
     * 활동 설명
     */
    String description() default "";

    /**
     * 참조 엔티티 유형 (예: ADMIN, MEMBER, PRODUCT)
     */
    String referenceType() default "";

    /**
     * 참조 ID를 가져올 메서드 이름 (기본값: getId)
     */
    String referenceIdMethod() default "getId";

    /**
     * 추가 데이터를 로깅할지 여부
     */
    boolean logParams() default false;
}