package com.peakmeshop.common.event;

import com.peakmeshop.common.security.CustomUserDetailsService;
import com.peakmeshop.domain.entity.ActivityLog;
import com.peakmeshop.domain.repository.ActivityLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationEventListener {

    private final ActivityLogRepository activityLogRepository;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        String userId = authentication.getName();
        Long memberId = null;

        if (authentication.getPrincipal() instanceof CustomUserDetailsService.CustomUserDetails) {
            CustomUserDetailsService.CustomUserDetails userDetails =
                    (CustomUserDetailsService.CustomUserDetails) authentication.getPrincipal();
            memberId = userDetails.getMemberId();
        }

        String ipAddress = "0.0.0.0";
        String userAgent = "Unknown";

        if (authentication.getDetails() instanceof WebAuthenticationDetails) {
            WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
            ipAddress = details.getRemoteAddress();
        }

        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            userAgent = request.getHeader("User-Agent");
        } catch (Exception e) {
            log.warn("HTTP 요청 정보를 가져올 수 없습니다.", e);
        }

        ActivityLog activityLog = ActivityLog.builder()
                .type("LOGIN")
                .description("로그인 성공")
                .referenceType("MEMBER")
                .referenceId(memberId)
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .userId(userId)
                .memberId(memberId)
                .createdAt(LocalDateTime.now())
                .build();

        activityLogRepository.save(activityLog);
        log.info("로그인 활동 로그 저장 완료: {}", userId);
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        Authentication authentication = event.getAuthentication();
        String userId = authentication.getName();

        String ipAddress = "0.0.0.0";
        String userAgent = "Unknown";

        if (authentication.getDetails() instanceof WebAuthenticationDetails) {
            WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
            ipAddress = details.getRemoteAddress();
        }

        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            userAgent = request.getHeader("User-Agent");
        } catch (Exception e) {
            log.warn("HTTP 요청 정보를 가져올 수 없습니다.", e);
        }

        ActivityLog activityLog = ActivityLog.builder()
                .type("LOGIN_FAILED")
                .description("로그인 실패: " + event.getException().getMessage())
                .referenceType("MEMBER")
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build();

        activityLogRepository.save(activityLog);
        log.info("로그인 실패 활동 로그 저장 완료: {}", userId);
    }

    @EventListener
    public void onLogout(LogoutSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        String userId = authentication.getName();
        Long memberId = null;

        if (authentication.getPrincipal() instanceof CustomUserDetailsService.CustomUserDetails) {
            CustomUserDetailsService.CustomUserDetails userDetails =
                    (CustomUserDetailsService.CustomUserDetails) authentication.getPrincipal();
            memberId = userDetails.getMemberId();
        }

        String ipAddress = "0.0.0.0";
        String userAgent = "Unknown";

        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            userAgent = request.getHeader("User-Agent");
            ipAddress = getClientIp(request);
        } catch (Exception e) {
            log.warn("HTTP 요청 정보를 가져올 수 없습니다.", e);
        }

        ActivityLog activityLog = ActivityLog.builder()
                .type("LOGOUT")
                .description("로그아웃 성공")
                .referenceType("MEMBER")
                .referenceId(memberId)
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .userId(userId)
                .memberId(memberId)
                .createdAt(LocalDateTime.now())
                .build();

        activityLogRepository.save(activityLog);
        log.info("로그아웃 활동 로그 저장 완료: {}", userId);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || "unknown".equalsIgnoreCase(xfHeader)) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}