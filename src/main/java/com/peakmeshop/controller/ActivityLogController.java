package com.peakmeshop.controller;

import com.peakmeshop.dto.ActivityLogDTO;
import com.peakmeshop.security.oauth2.user.UserPrincipal;
import com.peakmeshop.service.ActivityLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @PostMapping
    public ResponseEntity<Void> logActivity(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody ActivityLogDTO activityLogDTO,
            HttpServletRequest request) {

        if (userPrincipal != null) {
            // 로그인한 사용자의 활동 로깅
            activityLogService.logMemberActivity(
                    userPrincipal.getId(),
                    activityLogDTO.actionType(),
                    activityLogDTO.entityType(),
                    activityLogDTO.entityId(),
                    activityLogDTO.description());
        } else {
            // 비로그인 사용자의 활동 로깅
            activityLogService.logGuestActivity(
                    request.getSession().getId(),
                    request.getRemoteAddr(),
                    request.getHeader("User-Agent"),
                    activityLogDTO.actionType(),
                    activityLogDTO.entityType(),
                    activityLogDTO.entityId(),
                    activityLogDTO.description());
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/member")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ActivityLogDTO>> getMemberActivityLogs(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(activityLogService.getMemberActivityLogs(userPrincipal.getId(), pageable));
    }

    @GetMapping("/action-type/{actionType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ActivityLogDTO>> getActivityLogsByActionType(
            @PathVariable String actionType,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(activityLogService.getActivityLogsByActionType(actionType, pageable));
    }

    @GetMapping("/entity-type/{entityType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ActivityLogDTO>> getActivityLogsByEntityType(
            @PathVariable String entityType,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(activityLogService.getActivityLogsByEntityType(entityType, pageable));
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ActivityLogDTO>> getActivityLogsByEntityId(
            @PathVariable String entityType,
            @PathVariable Long entityId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(activityLogService.getActivityLogsByEntityId(entityType, entityId, pageable));
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ActivityLogDTO>> getActivityLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(activityLogService.getActivityLogsByDateRange(startDate, endDate, pageable));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ActivityLogDTO>> searchActivityLogs(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(activityLogService.searchActivityLogs(keyword, pageable));
    }

    @GetMapping("/stats/action-type")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getActivityStatsByActionType(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(activityLogService.getActivityStatsByActionType(startDate, endDate));
    }

    @GetMapping("/stats/entity-type")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getActivityStatsByEntityType(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(activityLogService.getActivityStatsByEntityType(startDate, endDate));
    }

    @GetMapping("/stats/hour-of-day")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getActivityStatsByHourOfDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(activityLogService.getActivityStatsByHourOfDay(startDate, endDate));
    }

    @GetMapping("/stats/day-of-week")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getActivityStatsByDayOfWeek(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(activityLogService.getActivityStatsByDayOfWeek(startDate, endDate));
    }

    @PostMapping("/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cleanupOldActivityLogs(@RequestParam int daysToKeep) {
        activityLogService.cleanupOldActivityLogs(daysToKeep);
        return ResponseEntity.ok().build();
    }
}