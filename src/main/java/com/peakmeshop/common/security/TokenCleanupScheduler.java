package com.peakmeshop.common.security;

import com.peakmeshop.common.security.CustomPersistentTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupScheduler {

    private final CustomPersistentTokenRepository tokenRepository;

    /**
     * 매일 자정에 30일 이상 지난 만료된 토큰을 정리합니다.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredTokens() {
        log.info("만료된 Remember Me 토큰 정리 작업 시작");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        Date expiryDate = calendar.getTime();

        tokenRepository.cleanUpExpiredTokens(expiryDate);

        log.info("만료된 Remember Me 토큰 정리 작업 완료");
    }
}