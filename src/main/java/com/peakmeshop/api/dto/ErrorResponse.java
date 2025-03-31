package com.peakmeshop.api.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * API 오류 응답을 위한 DTO
 */
public record ErrorResponse(
        String status,
        int code,
        String message,
        List<FieldError> errors,
        String path,
        LocalDateTime timestamp
) {
    /**
     * 필드 오류 정보
     */
    public record FieldError(
            String field,
            String message,
            String rejectedValue
    ) {}

    /**
     * 기본 오류 응답 생성
     * @param status 상태
     * @param code 코드
     * @param message 메시지
     * @param path 경로
     * @return 오류 응답
     */
    public static ErrorResponse of(String status, int code, String message, String path) {
        return new ErrorResponse(
                status,
                code,
                message,
                new ArrayList<>(),
                path,
                LocalDateTime.now()
        );
    }

    /**
     * 필드 오류가 있는 오류 응답 생성
     * @param status 상태
     * @param code 코드
     * @param message 메시지
     * @param errors 필드 오류 목록
     * @param path 경로
     * @return 오류 응답
     */
    public static ErrorResponse of(String status, int code, String message, List<FieldError> errors, String path) {
        return new ErrorResponse(
                status,
                code,
                message,
                errors,
                path,
                LocalDateTime.now()
        );
    }
}