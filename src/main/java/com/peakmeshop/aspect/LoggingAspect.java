package com.peakmeshop.aspect;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 모든 컨트롤러 메서드에 대한 포인트컷
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerPointcut() {
        // 메서드 본문 필요 없음
    }

    /**
     * 모든 서비스 메서드에 대한 포인트컷
     */
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void servicePointcut() {
        // 메서드 본문 필요 없음
    }

    /**
     * 모든 리포지토리 메서드에 대한 포인트컷
     */
    @Pointcut("within(@org.springframework.stereotype.Repository *)")
    public void repositoryPointcut() {
        // 메서드 본문 필요 없음
    }

    /**
     * 컨트롤러, 서비스, 리포지토리 메서드 실행 시간 로깅
     */
    @Around("controllerPointcut() || servicePointcut() || repositoryPointcut()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;

            // 실행 시간이 1초 이상인 경우에만 로깅
            if (executionTime > 1000) {
                log.warn("실행 시간 경고: {}.{}() - {}ms", className, methodName, executionTime);
            } else if (log.isDebugEnabled()) {
                log.debug("{}.{}() - 실행 시간: {}ms", className, methodName, executionTime);
            }

            return result;
        } catch (Exception e) {
            log.error("{}.{}() - 실행 중 오류 발생: {}", className, methodName, e.getMessage());
            throw e;
        }
    }

    /**
     * 예외 발생 시 로깅
     */
    @AfterThrowing(pointcut = "controllerPointcut() || servicePointcut() || repositoryPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.error("예외 발생: {}.{}() - 예외 유형: {}, 메시지: {}, 인자: {}",
                className, methodName, e.getClass().getName(), e.getMessage(), Arrays.toString(args));
    }
}