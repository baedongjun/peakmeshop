package com.peakmeshop.common.thymeleaf;

import jakarta.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Thymeleaf 템플릿에서 HttpSession에 접근할 수 있게 해주는 객체
 */
public class SessionObject {

    private final HttpSession session;

    public SessionObject(HttpSession session) {
        this.session = session;
    }

    /**
     * 세션 ID를 반환합니다.
     */
    public String getId() {
        return session.getId();
    }

    /**
     * 세션 생성 시간을 반환합니다.
     */
    public long getCreationTime() {
        return session.getCreationTime();
    }

    /**
     * 마지막 접근 시간을 반환합니다.
     */
    public long getLastAccessedTime() {
        return session.getLastAccessedTime();
    }

    /**
     * 세션이 새로 생성되었는지 확인합니다.
     */
    public boolean isNew() {
        return session.isNew();
    }

    /**
     * 세션 속성을 반환합니다.
     */
    public Object getAttribute(String name) {
        return session.getAttribute(name);
    }

    /**
     * 모든 세션 속성을 맵으로 반환합니다.
     */
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        Enumeration<String> names = session.getAttributeNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            attributes.put(name, session.getAttribute(name));
        }
        return attributes;
    }

    /**
     * 세션 최대 유효 시간을 반환합니다.
     */
    public int getMaxInactiveInterval() {
        return session.getMaxInactiveInterval();
    }
}

