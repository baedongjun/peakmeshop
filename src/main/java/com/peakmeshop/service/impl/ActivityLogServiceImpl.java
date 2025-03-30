package com.peakmeshop.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.dto.ActivityLogDTO;
import com.peakmeshop.entity.ActivityLog;
import com.peakmeshop.repository.ActivityLogRepository;
import com.peakmeshop.service.ActivityLogService;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogServiceImpl(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityLogDTO> getAllActivityLogs(Pageable pageable) {
        return activityLogRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ActivityLogDTO> getActivityLogById(Long id) {
        return activityLogRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityLogDTO> getMemberActivityLogs(Long memberId, Pageable pageable) {
        return activityLogRepository.findByMemberId(memberId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityLogDTO> getActivityLogsByActionType(String actionType, Pageable pageable) {
        return activityLogRepository.findByActionType(actionType, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityLogDTO> getActivityLogsByEntityType(String entityType, Pageable pageable) {
        return activityLogRepository.findByEntityType(entityType, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityLogDTO> getActivityLogsByEntityId(String entityType, Long entityId, Pageable pageable) {
        return activityLogRepository.findByEntityTypeAndEntityId(entityType, entityId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityLogDTO> getActivityLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return activityLogRepository.findByCreatedAtBetween(startDate, endDate, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityLogDTO> searchActivityLogs(String keyword, Pageable pageable) {
        return activityLogRepository.searchByKeyword(keyword, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getActivityStatsByActionType(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = activityLogRepository.countByActionTypeAndCreatedAtBetween(startDate, endDate);
        List<Map<String, Object>> stats = new ArrayList<>();

        for (Object[] result : results) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("actionType", result[0]);
            stat.put("count", result[1]);
            stats.add(stat);
        }

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getActivityStatsByEntityType(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = activityLogRepository.countByEntityTypeAndCreatedAtBetween(startDate, endDate);
        List<Map<String, Object>> stats = new ArrayList<>();

        for (Object[] result : results) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("entityType", result[0]);
            stat.put("count", result[1]);
            stats.add(stat);
        }

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getActivityStatsByHourOfDay(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = activityLogRepository.countByHourOfDayAndCreatedAtBetween(startDate, endDate);
        List<Map<String, Object>> stats = new ArrayList<>();

        for (Object[] result : results) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("hour", result[0]);
            stat.put("count", result[1]);
            stats.add(stat);
        }

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getActivityStatsByDayOfWeek(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = activityLogRepository.countByDayOfWeekAndCreatedAtBetween(startDate, endDate);
        List<Map<String, Object>> stats = new ArrayList<>();

        for (Object[] result : results) {
            Map<String, Object> stat = new HashMap<>();
            int dayOfWeekNum = (Integer) result[0];
            String dayOfWeek = DayOfWeek.of(dayOfWeekNum).name();
            stat.put("dayOfWeek", dayOfWeek);
            stat.put("count", result[1]);
            stats.add(stat);
        }

        return stats;
    }

    @Override
    @Transactional
    public ActivityLogDTO logMemberActivity(Long memberId, String actionType, String entityType, Long entityId, String description) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setMemberId(memberId);
        activityLog.setActionType(actionType);
        activityLog.setEntityType(entityType);
        activityLog.setEntityId(entityId);
        activityLog.setDescription(description);
        activityLog.setCreatedAt(LocalDateTime.now());

        ActivityLog savedLog = activityLogRepository.save(activityLog);
        return convertToDTO(savedLog);
    }

    @Override
    @Transactional
    public ActivityLogDTO logGuestActivity(String ipAddress, String userAgent, String actionType, String entityType, String entityId, Long entityNumericId, String description) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setIpAddress(ipAddress);
        activityLog.setUserAgent(userAgent);
        activityLog.setActionType(actionType);
        activityLog.setEntityType(entityType);
        // entityId는 String인데 엔티티는 Long을 저장하므로 entityNumericId 사용
        activityLog.setEntityId(entityNumericId);
        activityLog.setDescription(description);
        activityLog.setCreatedAt(LocalDateTime.now());

        ActivityLog savedLog = activityLogRepository.save(activityLog);
        return convertToDTO(savedLog);
    }

    @Override
    @Transactional
    public ActivityLogDTO createActivityLog(ActivityLogDTO activityLogDTO) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setMemberId(activityLogDTO.memberId());
        activityLog.setActionType(activityLogDTO.actionType());
        activityLog.setEntityType(activityLogDTO.entityType());
        activityLog.setEntityId(activityLogDTO.entityId());
        activityLog.setDescription(activityLogDTO.description());
        activityLog.setIpAddress(activityLogDTO.ipAddress());
        activityLog.setUserAgent(activityLogDTO.userAgent());
        activityLog.setCreatedAt(LocalDateTime.now());

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
    public void cleanupOldActivityLogs(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        activityLogRepository.deleteByCreatedAtBefore(cutoffDate);
    }

    private ActivityLogDTO convertToDTO(ActivityLog activityLog) {
        return new ActivityLogDTO(
                activityLog.getId(),
                activityLog.getMemberId(),
                activityLog.getActionType(),
                activityLog.getEntityType(),
                activityLog.getEntityId(),
                activityLog.getDescription(),
                activityLog.getIpAddress(),
                activityLog.getUserAgent(),
                activityLog.getCreatedAt()
        );
    }
}