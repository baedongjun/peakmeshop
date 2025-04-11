package com.peakmeshop.common.event;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        // 로그인 실패 시 추가 처리 (이벤트는 Spring Security에서 자동으로 발행됨)

        // 로그인 실패 후 리다이렉트
        response.sendRedirect("/login?error=" + exception.getMessage());
    }
}