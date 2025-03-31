package com.peakmeshop.domain.service;

import com.peakmeshop.api.dto.ActivityLogDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ActivityLogService {
    Page<ActivityLogDTO> getAllActivityLogs(Pageable pageable);
    Optional<ActivityLogDTO> getActivityLogById(Long id);
    Page<ActivityLogDTO> getMemberActivityLogs(Long memberId, Pageable pageable);
    Page<ActivityLogDTO> getActivityLogsByActionType(String actionType, Pageable pageable);
    Page<ActivityLogDTO> getActivityLogsByEntityType(String entityType, Pageable pageable);
    Page<ActivityLogDTO> getActivityLogsByEntityId(String entityType, Long entityId, Pageable pageable);
    Page<ActivityLogDTO> getActivityLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Page<ActivityLogDTO> searchActivityLogs(String keyword, Pageable pageable);

    // 통계 메서드 반환 타입 수정
    List<Map<String, Object>> getActivityStatsByActionType(LocalDateTime startDate, LocalDateTime endDate);
    List<Map<String, Object>> getActivityStatsByEntityType(LocalDateTime startDate, LocalDateTime endDate);
    List<Map<String, Object>> getActivityStatsByHourOfDay(LocalDateTime startDate, LocalDateTime endDate);
    List<Map<String, Object>> getActivityStatsByDayOfWeek(LocalDateTime startDate, LocalDateTime endDate);

    // 누락된 로깅 메서드 추가
    ActivityLogDTO logMemberActivity(Long memberId, String actionType, String entityType, Long entityId, String description);
    ActivityLogDTO logGuestActivity(String ipAddress, String userAgent, String actionType, String entityType, String entityId, Long entityNumericId, String description);

    ActivityLogDTO createActivityLog(ActivityLogDTO activityLogDTO);
    void deleteActivityLog(Long id);
    void cleanupOldActivityLogs(int days);
}