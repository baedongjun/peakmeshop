package com.peakmeshop.domain.service;

import com.peakmeshop.api.dto.ActivityLogDTO;
import com.peakmeshop.domain.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ActivityLogService {
    /**
     * 최근 활동 목록을 조회합니다.
     * @param limit 조회할 활동 수
     * @return 최근 활동 목록
     */
    List<ActivityLogDTO> getRecentActivities(int limit);

    /**
     * 특정 관리자의 활동 목록을 조회합니다.
     * @param adminId 관리자 ID
     * @param limit 조회할 활동 수
     * @return 관리자 활동 목록
     */
    List<ActivityLogDTO> getAdminActivities(Long adminId, int limit);

    /**
     * ActivityLog 엔티티를 ActivityLogDTO로 변환합니다.
     * @param activityLog ActivityLog 엔티티
     * @return ActivityLogDTO
     */
    ActivityLogDTO convertToDTO(ActivityLog activityLog);

    /**
     * 활동 로그 목록을 조회합니다.
     * @param type 활동 유형
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param keyword 검색어
     * @param pageable 페이징 정보
     * @return 활동 로그 목록
     */
    Page<ActivityLogDTO> getActivityLogs(String type, LocalDateTime startDate, LocalDateTime endDate, String keyword, Pageable pageable);

    /**
     * 활동 로그를 조회합니다.
     * @param id 활동 로그 ID
     * @return 활동 로그
     */
    ActivityLogDTO getActivityLog(Long id);

    /**
     * 활동 로그를 생성합니다.
     * @param activityLogDTO 활동 로그 DTO
     * @return 생성된 활동 로그
     */
    ActivityLogDTO createActivityLog(ActivityLogDTO activityLogDTO);

    /**
     * 활동 로그를 삭제합니다.
     * @param id 활동 로그 ID
     */
    void deleteActivityLog(Long id);

    /**
     * 활동 로그를 일괄 삭제합니다.
     * @param ids 활동 로그 ID 목록
     */
    void deleteActivityLogs(List<Long> ids);

    /**
     * 활동 유형 목록을 조회합니다.
     * @return 활동 유형 목록
     */
    List<String> getActivityTypes();

    /**
     * 활동 유형별 통계를 조회합니다.
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 활동 유형별 통계
     */
    Map<String, Long> getActivityTypeStats(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 일별 활동 통계를 조회합니다.
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 일별 활동 통계
     */
    Map<String, Long> getDailyActivityStats(LocalDateTime startDate, LocalDateTime endDate);
}