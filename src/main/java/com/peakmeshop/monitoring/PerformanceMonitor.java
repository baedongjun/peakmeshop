package com.peakmeshop.monitoring;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PerformanceMonitor {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitor.class);

    private final Map<String, AtomicInteger> apiCallCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> errorCounts = new ConcurrentHashMap<>();

    /**
     * API 호출 카운트 증가
     * @param endpoint API 엔드포인트
     */
    public void incrementApiCall(String endpoint) {
        apiCallCounts.computeIfAbsent(endpoint, k -> new AtomicInteger(0)).incrementAndGet();
    }

    /**
     * 오류 카운트 증가
     * @param endpoint API 엔드포인트
     */
    public void incrementError(String endpoint) {
        errorCounts.computeIfAbsent(endpoint, k -> new AtomicInteger(0)).incrementAndGet();
    }

    /**
     * 시스템 성능 지표 수집
     * @return 성능 지표 맵
     */
    public Map<String, Object> collectPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        // JVM 메모리 사용량
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();

        metrics.put("heap.used", heapMemoryUsage.getUsed());
        metrics.put("heap.max", heapMemoryUsage.getMax());
        metrics.put("heap.committed", heapMemoryUsage.getCommitted());
        metrics.put("nonHeap.used", nonHeapMemoryUsage.getUsed());
        metrics.put("nonHeap.committed", nonHeapMemoryUsage.getCommitted());

        // CPU 사용량
        double systemLoad = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
        metrics.put("cpu.systemLoad", systemLoad);

        // 스레드 정보
        int threadCount = ManagementFactory.getThreadMXBean().getThreadCount();
        metrics.put("threads.count", threadCount);

        // API 호출 통계
        metrics.put("api.calls", new HashMap<>(apiCallCounts));
        metrics.put("api.errors", new HashMap<>(errorCounts));

        return metrics;
    }

    /**
     * 5분마다 성능 지표 로깅
     */
    @Scheduled(fixedRate = 300000) // 5분마다 실행
    public void logPerformanceMetrics() {
        Map<String, Object> metrics = collectPerformanceMetrics();

        logger.info("=== 시스템 성능 지표 ===");
        logger.info("힙 메모리 사용량: {}/{} MB ({}%)",
                bytesToMB((long) metrics.get("heap.used")),
                bytesToMB((long) metrics.get("heap.max")),
                calculatePercentage((long) metrics.get("heap.used"), (long) metrics.get("heap.max")));
        logger.info("논힙 메모리 사용량: {} MB",
                bytesToMB((long) metrics.get("nonHeap.used")));
        logger.info("시스템 로드: {}", metrics.get("cpu.systemLoad"));
        logger.info("스레드 수: {}", metrics.get("threads.count"));

        @SuppressWarnings("unchecked")
        Map<String, AtomicInteger> apiCalls = (Map<String, AtomicInteger>) metrics.get("api.calls");
        @SuppressWarnings("unchecked")
        Map<String, AtomicInteger> apiErrors = (Map<String, AtomicInteger>) metrics.get("api.errors");

        if (!apiCalls.isEmpty()) {
            logger.info("=== API 호출 통계 ===");
            apiCalls.forEach((endpoint, count) -> {
                int errorCount = apiErrors.getOrDefault(endpoint, new AtomicInteger(0)).get();
                double errorRate = count.get() > 0 ? (double) errorCount / count.get() * 100 : 0;

                logger.info("{}: {} 호출, {} 오류 (오류율: {:.2f}%)",
                        endpoint, count.get(), errorCount, errorRate);
            });
        }

        // 통계 초기화
        apiCallCounts.clear();
        errorCounts.clear();
    }

    private double bytesToMB(long bytes) {
        return bytes / (1024.0 * 1024.0);
    }

    private double calculatePercentage(long used, long max) {
        return max > 0 ? (double) used / max * 100 : 0;
    }
}