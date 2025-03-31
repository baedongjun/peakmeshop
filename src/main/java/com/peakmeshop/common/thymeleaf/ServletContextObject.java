package com.peakmeshop.common.thymeleaf;

import jakarta.servlet.ServletContext;

/**
 * Thymeleaf 템플릿에서 ServletContext에 접근할 수 있게 해주는 객체
 */
public class ServletContextObject {

    private final ServletContext servletContext;

    public ServletContextObject(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * 컨텍스트 경로를 반환합니다.
     */
    public String getContextPath() {
        return servletContext.getContextPath();
    }

    /**
     * 서버 정보를 반환합니다.
     */
    public String getServerInfo() {
        return servletContext.getServerInfo();
    }

    /**
     * 서블릿 컨텍스트 이름을 반환합니다.
     */
    public String getServletContextName() {
        return servletContext.getServletContextName();
    }

    /**
     * 초기화 파라미터를 반환합니다.
     */
    public String getInitParameter(String name) {
        return servletContext.getInitParameter(name);
    }

    /**
     * 속성을 반환합니다.
     */
    public Object getAttribute(String name) {
        return servletContext.getAttribute(name);
    }
}

