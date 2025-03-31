package com.peakmeshop.common.thymeleaf;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Thymeleaf 템플릿에서 HttpServletResponse에 접근할 수 있게 해주는 객체
 */
public class ResponseObject {

    private final HttpServletResponse response;

    public ResponseObject(HttpServletResponse response) {
        this.response = response;
    }

    /**
     * 응답 상태 코드를 반환합니다.
     */
    public int getStatus() {
        return response.getStatus();
    }

    /**
     * 응답 콘텐츠 타입을 반환합니다.
     */
    public String getContentType() {
        return response.getContentType();
    }

    /**
     * 응답 헤더를 반환합니다.
     */
    public String getHeader(String name) {
        return response.getHeader(name);
    }

    /**
     * 응답 문자 인코딩을 반환합니다.
     */
    public String getCharacterEncoding() {
        return response.getCharacterEncoding();
    }
}

