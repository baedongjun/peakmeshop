package com.peakmeshop.common.filter;

import java.io.IOException;
import java.util.UUID;

import com.peakmeshop.common.monitoring.PerformanceMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    private final PerformanceMonitor performanceMonitor;

    public RequestLoggingFilter(PerformanceMonitor performanceMonitor) {
        this.performanceMonitor = performanceMonitor;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 요청 ID 생성 및 MDC에 설정
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        // 요청 시작 시간
        long startTime = System.currentTimeMillis();

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String endpoint = method + " " + uri;

        // 요청 로깅
        logger.info("요청 시작: {} {} (쿼리: {})", method, uri, queryString);

        try {
            // API 호출 카운트 증가
            performanceMonitor.incrementApiCall(endpoint);

            // 다음 필터 실행
            filterChain.doFilter(request, response);

            // 응답 로깅
            int status = response.getStatus();
            long duration = System.currentTimeMillis() - startTime;

            logger.info("응답 완료: {} {} - 상태: {}, 소요 시간: {}ms", method, uri, status, duration);

            // 오류 응답인 경우 오류 카운트 증가
            if (status >= 400) {
                performanceMonitor.incrementError(endpoint);
            }
        } catch (Exception e) {
            // 예외 발생 시 로깅
            logger.error("요청 처리 중 오류 발생: {} {} - {}", method, uri, e.getMessage(), e);

            // 오류 카운트 증가
            performanceMonitor.incrementError(endpoint);

            throw e;
        } finally {
            // MDC 정리
            MDC.remove("requestId");
        }
    }
}