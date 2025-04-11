package com.peakmeshop.api.controller;

import com.peakmeshop.api.dto.ActivityLogDTO;
import com.peakmeshop.domain.entity.ActivityLog;
import com.peakmeshop.domain.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 활동 로그 관련 컨트롤러
 */
@Controller
@RequestMapping("/api/activityLog")
@RequiredArgsConstructor
@Slf4j
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    /**
     * 활동 로그 목록 페이지
     */
    @GetMapping
    public String activityLogs(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        // 활동 로그 목록 조회
        Page<ActivityLogDTO> activityLogs = activityLogService.getActivityLogs(type, startDate, endDate, keyword, pageable);
        model.addAttribute("activityLogs", activityLogs);

        // 활동 유형 목록 조회
        List<String> activityTypes = activityLogService.getActivityTypes();
        model.addAttribute("activityTypes", activityTypes);

        // 검색 조건 유지
        model.addAttribute("type", type);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("keyword", keyword);

        return "admin/activity-logs";
    }

    /**
     * 활동 로그 상세 페이지
     */
    @GetMapping("/{id}")
    public String activityLogDetail(@PathVariable Long id, Model model) {
        ActivityLogDTO activityLog = activityLogService.getActivityLog(id);
        model.addAttribute("activityLog", activityLog);

        return "admin/activity-log-detail";
    }

    /**
     * 활동 로그 삭제
     */
    @PostMapping("/{id}/delete")
    public String deleteActivityLog(@PathVariable Long id) {
        activityLogService.deleteActivityLog(id);
        return "redirect:/admin/activity-logs";
    }

    /**
     * 활동 로그 일괄 삭제
     */
    @PostMapping("/delete")
    public String deleteActivityLogs(@RequestParam List<Long> ids) {
        activityLogService.deleteActivityLogs(ids);
        return "redirect:/admin/activity-logs";
    }

    /**
     * 활동 로그 통계 페이지
     */
    @GetMapping("/stats")
    public String activityLogStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Model model
    ) {
        // 기본값 설정
        if (startDate == null) {
            startDate = LocalDateTime.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        // 활동 유형별 통계
        Map<String, Long> typeStats = activityLogService.getActivityTypeStats(startDate, endDate);
        model.addAttribute("typeStats", typeStats);

        // 일별 활동 통계
        Map<String, Long> dailyStats = activityLogService.getDailyActivityStats(startDate, endDate);
        model.addAttribute("dailyStats", dailyStats);

        // 검색 조건 유지
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "admin/activity-log-stats";
    }

    /**
     * 최근 활동 로그 API
     */
    @GetMapping("/api/recent")
    @ResponseBody
    public ResponseEntity<List<ActivityLogDTO>> getRecentActivityLogs(
            @RequestParam(defaultValue = "5") int limit
    ) {
        List<ActivityLogDTO> recentActivities = activityLogService.getRecentActivities(limit);
        return ResponseEntity.ok(recentActivities);
    }

    /**
     * 활동 로그 생성 API
     */
    @PostMapping("/api/create")
    @ResponseBody
    public ResponseEntity<ActivityLogDTO> createActivityLog(@RequestBody ActivityLogDTO activityLogDTO) {
        ActivityLogDTO createdLog = activityLogService.createActivityLog(activityLogDTO);
        return ResponseEntity.ok(createdLog);
    }

    /**
     * 활동 로그 목록 API
     */
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<Page<ActivityLogDTO>> getActivityLogs(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ActivityLogDTO> activityLogs = activityLogService.getActivityLogs(type, startDate, endDate, keyword, pageable);
        return ResponseEntity.ok(activityLogs);
    }

    /**
     * 활동 로그 상세 API
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<ActivityLogDTO> getActivityLog(@PathVariable Long id) {
        ActivityLogDTO activityLog = activityLogService.getActivityLog(id);
        return ResponseEntity.ok(activityLog);
    }

    /**
     * 활동 로그 삭제 API
     */
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteActivityLogApi(@PathVariable Long id) {
        activityLogService.deleteActivityLog(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 활동 로그 일괄 삭제 API
     */
    @DeleteMapping("/api")
    @ResponseBody
    public ResponseEntity<Void> deleteActivityLogsApi(@RequestBody List<Long> ids) {
        activityLogService.deleteActivityLogs(ids);
        return ResponseEntity.ok().build();
    }
}