package com.peakmeshop.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peakmeshop.common.annotation.LogActivity;
import com.peakmeshop.common.security.CustomUserDetailsService;
import com.peakmeshop.domain.entity.ActivityLog;
import com.peakmeshop.domain.repository.ActivityLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * 활동 로그를 기록하는 Aspect
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ActivityLogAspect {

    private final ActivityLogRepository activityLogRepository;
    private final ObjectMapper objectMapper;

    /**
     * LogActivity 어노테이션이 붙은 메서드에 대한 포인트컷
     */
    @Pointcut("@annotation(com.peakmeshop.common.annotation.LogActivity)")
    public void logActivityPointcut() {}

    /**
     * 메서드 실행 후 활동 로그 기록
     */
    @AfterReturning(pointcut = "logActivityPointcut()", returning = "result")
    public void logActivity(JoinPoint joinPoint, Object result) {
        try {
            // 메서드 정보 가져오기
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            // LogActivity 어노테이션 가져오기
            LogActivity logActivity = method.getAnnotation(LogActivity.class);

            // 현재 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = "anonymous";
            Long memberId = null;

            if (authentication != null && authentication.isAuthenticated() &&
                    !authentication.getPrincipal().equals("anonymousUser")) {
                userId = authentication.getName();

                if (authentication.getPrincipal() instanceof CustomUserDetailsService.CustomUserDetails) {
                    CustomUserDetailsService.CustomUserDetails userDetails =
                            (CustomUserDetailsService.CustomUserDetails) authentication.getPrincipal();
                    memberId = userDetails.getMemberId();
                }
            }

            // HTTP 요청 정보 가져오기
            String userAgent = "Unknown";
            String ipAddress = "0.0.0.0";

            try {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                userAgent = request.getHeader("User-Agent");
                ipAddress = getClientIp(request);
            } catch (Exception e) {
                log.warn("HTTP 요청 정보를 가져올 수 없습니다.", e);
            }

            // 메서드 파라미터 정보 가져오기
            String additionalData = null;
            if (logActivity.logParams()) {
                additionalData = getParametersAsJson(joinPoint);
            }

            // 참조 ID 결정
            Long referenceId = determineReferenceId(result, logActivity.referenceIdMethod());

            // 활동 로그 생성
            ActivityLog activityLog = ActivityLog.builder()
                    .type(logActivity.type())
                    .description(logActivity.description())
                    .referenceType(logActivity.referenceType())
                    .referenceId(referenceId)
                    .userAgent(userAgent)
                    .ipAddress(ipAddress)
                    .additionalData(additionalData)
                    .userId(userId)
                    .createdAt(LocalDateTime.now())
                    .build();

            // 로그 저장
            activityLogRepository.save(activityLog);
            log.debug("활동 로그 저장 완료: {}", logActivity.type());

        } catch (Exception e) {
            log.error("활동 로그 기록 중 오류 발생", e);
        }
    }

    /**
     * 클라이언트 IP 주소 가져오기
     */
    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || "unknown".equalsIgnoreCase(xfHeader)) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    /**
     * 참조 ID 결정하기
     */
    private Long determineReferenceId(Object result, String methodName) {
        if (result == null || methodName.isEmpty()) {
            return null;
        }

        try {
            Method method = result.getClass().getMethod(methodName);
            Object id = method.invoke(result);
            return id != null ? Long.valueOf(id.toString()) : null;
        } catch (Exception e) {
            log.warn("참조 ID 결정 중 오류 발생: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 메서드 파라미터를 JSON 형식으로 변환
     */
    private String getParametersAsJson(JoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] parameterNames = signature.getParameterNames();
            Object[] args = joinPoint.getArgs();

            Map<String, Object> params = new HashMap<>();
            IntStream.range(0, parameterNames.length)
                    .forEach(i -> {
                        // 민감한 정보는 마스킹 처리
                        if (parameterNames[i].toLowerCase().contains("password") ||
                                parameterNames[i].toLowerCase().contains("token") ||
                                parameterNames[i].toLowerCase().contains("secret")) {
                            params.put(parameterNames[i], "******");
                        } else {
                            params.put(parameterNames[i], args[i]);
                        }
                    });

            return objectMapper.writeValueAsString(params);
        } catch (Exception e) {
            log.warn("파라미터 변환 중 오류 발생: {}", e.getMessage());
            return Arrays.toString(joinPoint.getArgs());
        }
    }
}