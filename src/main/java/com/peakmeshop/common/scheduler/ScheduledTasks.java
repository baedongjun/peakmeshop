package com.peakmeshop.common.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.peakmeshop.domain.service.OrderService;
import com.peakmeshop.domain.service.PasswordResetTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {

    private final OrderService orderService;
    private final PasswordResetTokenService passwordResetTokenService;

    @Scheduled(cron = "0 0 1 * * ?") // 매일 새벽 1시에 실행
    public void cleanupTasks() {
        log.info("Running scheduled cleanup tasks");

        try {
            // 미결제 주문 취소
            orderService.cancelAbandonedOrders();
            log.info("Abandoned orders cleanup completed");

            // 만료된 비밀번호 재설정 토큰 정리
            passwordResetTokenService.cleanExpiredTokens();
            log.info("Expired password reset tokens cleanup completed");
        } catch (Exception e) {
            log.error("Error during scheduled cleanup tasks", e);
        }
    }
}