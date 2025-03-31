package com.peakmeshop.common.thymeleaf;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Thymeleaf 템플릿에서 HttpServletRequest에 접근할 수 있게 해주는 객체
 */
public class RequestObject {

    private final HttpServletRequest request;

    public RequestObject(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * 요청 URI를 반환합니다.
     */
    public String getRequestURI() {
        return request.getRequestURI();
    }

    /**
     * 컨텍스트 경로를 반환합니다.
     */
    public String getContextPath() {
        return request.getContextPath();
    }

    /**
     * 요청 메서드를 반환합니다.
     */
    public String getMethod() {
        return request.getMethod();
    }

    /**
     * 요청 파라미터를 반환합니다.
     */
    public String getParameter(String name) {
        return request.getParameter(name);
    }

    /**
     * 요청 헤더를 반환합니다.
     */
    public String getHeader(String name) {
        return request.getHeader(name);
    }

    /**
     * 요청 경로가 지정된 경로와 일치하는지 확인합니다.
     */
    public boolean isPath(String path) {
        return request.getRequestURI().equals(request.getContextPath() + path);
    }

    /**
     * 요청 경로가 지정된 경로로 시작하는지 확인합니다.
     */
    public boolean startsWith(String path) {
        return request.getRequestURI().startsWith(request.getContextPath() + path);
    }

    /**
     * 요청 경로가 지정된 경로로 끝나는지 확인합니다.
     */
    public boolean endsWith(String suffix) {
        return request.getRequestURI().endsWith(suffix);
    }

    /**
     * 요청 경로가 지정된 패턴을 포함하는지 확인합니다.
     */
    public boolean contains(String pattern) {
        return request.getRequestURI().contains(pattern);
    }
}

