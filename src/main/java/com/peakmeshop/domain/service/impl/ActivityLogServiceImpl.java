package com.peakmeshop.domain.service.impl;

import com.peakmeshop.api.dto.ActivityLogDTO;
import com.peakmeshop.domain.entity.ActivityLog;
import com.peakmeshop.domain.repository.ActivityLogRepository;
import com.peakmeshop.domain.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ActivityLogDTO> getRecentActivities(int limit) {
        // DB에서 최근 활동 로그를 가져옵니다.
        List<ActivityLog> activityLogs = activityLogRepository.findTopByOrderByCreatedAtDesc(limit);

        // DTO로 변환하고 시간순으로 정렬합니다.
        return activityLogs.stream()
                .map(this::convertToDTO)
                .sorted(Comparator.comparing(ActivityLogDTO::getCreatedAt).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityLogDTO> getAdminActivities(Long adminId, int limit) {
        // 특정 관리자의 활동 로그를 가져옵니다.
        List<ActivityLog> activityLogs = activityLogRepository.findByAdminId(adminId, limit);

        // DTO로 변환하고 시간순으로 정렬합니다.
        return activityLogs.stream()
                .map(this::convertToDTO)
                .sorted(Comparator.comparing(ActivityLogDTO::getCreatedAt).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityLogDTO> getActivityLogs(String type, LocalDateTime startDate, LocalDateTime endDate, String keyword, Pageable pageable) {
        Page<ActivityLog> activityLogs;

        if (type != null && !type.isEmpty() && startDate != null && endDate != null && keyword != null && !keyword.isEmpty()) {
            activityLogs = activityLogRepository.findByTypeAndCreatedAtBetweenAndDescriptionContainingIgnoreCase(
                    type, startDate, endDate, keyword, pageable);
        } else if (type != null && !type.isEmpty() && startDate != null && endDate != null) {
            activityLogs = activityLogRepository.findByTypeAndCreatedAtBetween(type, startDate, endDate, pageable);
        } else if (startDate != null && endDate != null && keyword != null && !keyword.isEmpty()) {
            activityLogs = activityLogRepository.findByCreatedAtBetweenAndDescriptionContainingIgnoreCase(
                    startDate, endDate, keyword, pageable);
        } else if (type != null && !type.isEmpty() && keyword != null && !keyword.isEmpty()) {
            activityLogs = activityLogRepository.findByTypeAndDescriptionContainingIgnoreCase(type, keyword, pageable);
        } else if (type != null && !type.isEmpty()) {
            activityLogs = activityLogRepository.findByType(type, pageable);
        } else if (startDate != null && endDate != null) {
            activityLogs = activityLogRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            activityLogs = activityLogRepository.findByDescriptionContainingIgnoreCase(keyword, pageable);
        } else {
            activityLogs = activityLogRepository.findAll(pageable);
        }

        List<ActivityLogDTO> activityLogDTOs = activityLogs.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(activityLogDTOs, pageable, activityLogs.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityLogDTO getActivityLog(Long id) {
        ActivityLog activityLog = activityLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("활동 로그를 찾을 수 없습니다."));

        return convertToDTO(activityLog);
    }

    @Override
    @Transactional
    public ActivityLogDTO createActivityLog(ActivityLogDTO activityLogDTO) {
        ActivityLog activityLog = ActivityLog.builder()
                .type(activityLogDTO.getType())
                .description(activityLogDTO.getDescription())
                .referenceId(activityLogDTO.getReferenceId())
                .referenceType(activityLogDTO.getReferenceType())
                .createdAt(LocalDateTime.now())
                .userId(activityLogDTO.getUserId())
                .memberId(activityLogDTO.getMemberId())
                .userAgent(activityLogDTO.getUserAgent())
                .build();

        ActivityLog savedActivityLog = activityLogRepository.save(activityLog);
        return convertToDTO(savedActivityLog);
    }

    @Override
    @Transactional
    public void deleteActivityLog(Long id) {
        activityLogRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteActivityLogs(List<Long> ids) {
        activityLogRepository.deleteAllById(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getActivityTypes() {
        return activityLogRepository.findDistinctTypes();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getActivityTypeStats(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> stats = activityLogRepository.countByTypeAndCreatedAtBetween(startDate, endDate);

        Map<String, Long> result = new HashMap<>();
        for (Object[] stat : stats) {
            String type = (String) stat[0];
            Long count = ((Number) stat[1]).longValue();
            result.put(type, count);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getDailyActivityStats(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> stats = activityLogRepository.countByDayAndCreatedAtBetween(startDate, endDate);

        Map<String, Long> result = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 모든 날짜에 대해 기본값 0으로 초기화
        LocalDateTime current = startDate;
        while (!current.isAfter(endDate)) {
            String day = current.format(formatter);
            result.put(day, 0L);
            current = current.plusDays(1);
        }

        // 실제 데이터로 업데이트
        for (Object[] stat : stats) {
            String day = (String) stat[0];
            Long count = ((Number) stat[1]).longValue();
            result.put(day, count);
        }

        return result;
    }

    @Override
    public ActivityLogDTO convertToDTO(ActivityLog activityLog) {
        LocalDateTime now = LocalDateTime.now();

        // 활동 유형에 따라 아이콘과 색상을 설정합니다.
        String icon = "fa-info-circle";
        String iconColor = "text-primary";

        switch (activityLog.getType()) {
            case "MEMBER_JOIN":
                icon = "fa-user-plus";
                iconColor = "text-success";
                break;
            case "NEW_ORDER":
                icon = "fa-shopping-cart";
                iconColor = "text-primary";
                break;
            case "LOW_STOCK":
                icon = "fa-box";
                iconColor = "text-warning";
                break;
            case "SHIPPING_START":
                icon = "fa-truck";
                iconColor = "text-info";
                break;
            case "NEW_REVIEW":
                icon = "fa-star";
                iconColor = "text-warning";
                break;
            case "ORDER_STATUS_CHANGE":
                icon = "fa-exchange-alt";
                iconColor = "text-info";
                break;
            case "PRODUCT_UPDATE":
                icon = "fa-edit";
                iconColor = "text-primary";
                break;
            case "COUPON_CREATED":
                icon = "fa-ticket-alt";
                iconColor = "text-success";
                break;
            case "PROMOTION_STARTED":
                icon = "fa-bullhorn";
                iconColor = "text-danger";
                break;
        }

        return ActivityLogDTO.builder()
                .id(activityLog.getId())
                .type(activityLog.getType())
                .description(activityLog.getDescription())
                .referenceId(activityLog.getReferenceId())
                .referenceType(activityLog.getReferenceType())
                .ipAddress(activityLog.getIpAddress())
                .additionalData(activityLog.getAdditionalData())
                .userId(activityLog.getUserId())
                .memberId(activityLog.getMemberId())
                .userAgent(activityLog.getUserAgent())
                .createdAt(activityLog.getCreatedAt())
                .build();
    }

    /**
     * 주어진 시간과 현재 시간의 차이를 문자열로 반환합니다.
     * @param dateTime 비교할 시간
     * @param now 현재 시간
     * @return 시간 차이 문자열 (예: "10분 전", "2시간 전")
     */
    private String getTimeAgo(LocalDateTime dateTime, LocalDateTime now) {
        Duration duration = Duration.between(dateTime, now);

        long seconds = duration.getSeconds();
        if (seconds < 60) {
            return seconds + "초 전";
        }

        long minutes = duration.toMinutes();
        if (minutes < 60) {
            return minutes + "분 전";
        }

        long hours = duration.toHours();
        if (hours < 24) {
            return hours + "시간 전";
        }

        long days = duration.toDays();
        if (days < 30) {
            return days + "일 전";
        }

        long months = days / 30;
        if (months < 12) {
            return months + "개월 전";
        }

        return (months / 12) + "년 전";
    }
}