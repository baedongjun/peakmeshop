package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    Page<ActivityLog> findByMemberId(Long memberId, Pageable pageable);
    Page<ActivityLog> findByActionType(String actionType, Pageable pageable);
    Page<ActivityLog> findByEntityType(String entityType, Pageable pageable);
    Page<ActivityLog> findByEntityTypeAndEntityId(String entityType, Long entityId, Pageable pageable);
    Page<ActivityLog> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("SELECT a FROM ActivityLog a WHERE a.description LIKE %:keyword% OR a.actionType LIKE %:keyword% OR a.entityType LIKE %:keyword%")
    Page<ActivityLog> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT a.actionType, COUNT(a) FROM ActivityLog a WHERE a.createdAt BETWEEN :startDate AND :endDate GROUP BY a.actionType")
    List<Object[]> countByActionTypeAndCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a.entityType, COUNT(a) FROM ActivityLog a WHERE a.createdAt BETWEEN :startDate AND :endDate GROUP BY a.entityType")
    List<Object[]> countByEntityTypeAndCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT EXTRACT(HOUR FROM a.created_at), COUNT(a) FROM activity_log a WHERE a.created_at BETWEEN :startDate AND :endDate GROUP BY EXTRACT(HOUR FROM a.created_at)", nativeQuery = true)
    List<Object[]> countByHourOfDayAndCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT EXTRACT(DOW FROM a.created_at), COUNT(a) FROM activity_log a WHERE a.created_at BETWEEN :startDate AND :endDate GROUP BY EXTRACT(DOW FROM a.created_at)", nativeQuery = true)
    List<Object[]> countByDayOfWeekAndCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    void deleteByCreatedAtBefore(LocalDateTime cutoffDate);
}