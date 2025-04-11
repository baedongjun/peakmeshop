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
    /**
     * 최근 활동 로그를 조회합니다.
     * @param limit 조회할 활동 수
     * @return 최근 활동 로그 목록
     */
    @Query(value = "SELECT * FROM activity_log ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<ActivityLog> findTopByOrderByCreatedAtDesc(@Param("limit") int limit);

    /**
     * 특정 관리자의 활동 로그를 조회합니다.
     * @param adminId 관리자 ID
     * @param limit 조회할 활동 수
     * @return 관리자 활동 로그 목록
     */
    @Query(value = "SELECT * FROM activity_log WHERE reference_id = :adminId AND reference_type = 'ADMIN' ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<ActivityLog> findByAdminId(@Param("adminId") Long adminId, @Param("limit") int limit);

    /**
     * 활동 유형별 활동 로그를 조회합니다.
     * @param type 활동 유형
     * @param pageable 페이징 정보
     * @return 활동 로그 목록
     */
    Page<ActivityLog> findByType(String type, Pageable pageable);

    /**
     * 특정 기간 내의 활동 로그를 조회합니다.
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param pageable 페이징 정보
     * @return 활동 로그 목록
     */
    Page<ActivityLog> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * 설명에 특정 키워드가 포함된 활동 로그를 조회합니다.
     * @param keyword 검색어
     * @param pageable 페이징 정보
     * @return 활동 로그 목록
     */
    Page<ActivityLog> findByDescriptionContainingIgnoreCase(String keyword, Pageable pageable);

    /**
     * 활동 유형과 설명에 특정 키워드가 포함된 활동 로그를 조회합니다.
     * @param type 활동 유형
     * @param keyword 검색어
     * @param pageable 페이징 정보
     * @return 활동 로그 목록
     */
    Page<ActivityLog> findByTypeAndDescriptionContainingIgnoreCase(String type, String keyword, Pageable pageable);

    /**
     * 특정 기간 내이고 설명에 특정 키워드가 포함된 활동 로그를 조회합니다.
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param keyword 검색어
     * @param pageable 페이징 정보
     * @return 활동 로그 목록
     */
    Page<ActivityLog> findByCreatedAtBetweenAndDescriptionContainingIgnoreCase(
            LocalDateTime startDate, LocalDateTime endDate, String keyword, Pageable pageable);

    /**
     * 활동 유형과 특정 기간 내의 활동 로그를 조회합니다.
     * @param type 활동 유형
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param pageable 페이징 정보
     * @return 활동 로그 목록
     */
    Page<ActivityLog> findByTypeAndCreatedAtBetween(
            String type, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * 활동 유형, 특정 기간 내, 설명에 특정 키워드가 포함된 활동 로그를 조회합니다.
     * @param type 활동 유형
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param keyword 검색어
     * @param pageable 페이징 정보
     * @return 활동 로그 목록
     */
    Page<ActivityLog> findByTypeAndCreatedAtBetweenAndDescriptionContainingIgnoreCase(
            String type, LocalDateTime startDate, LocalDateTime endDate, String keyword, Pageable pageable);

    /**
     * 고유한 활동 유형 목록을 조회합니다.
     * @return 활동 유형 목록
     */
    @Query("SELECT DISTINCT a.type FROM ActivityLog a ORDER BY a.type")
    List<String> findDistinctTypes();

    /**
     * 활동 유형별 활동 로그 수를 조회합니다.
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 활동 유형과 활동 로그 수를 포함하는 배열의 목록
     */
    @Query("SELECT a.type, COUNT(a) FROM ActivityLog a WHERE a.createdAt BETWEEN :startDate AND :endDate GROUP BY a.type")
    List<Object[]> countByTypeAndCreatedAtBetween(
            @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 일별 활동 로그 수를 조회합니다.
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 날짜와 활동 로그 수를 포함하는 배열의 목록
     */
    @Query(value =
            "SELECT TO_CHAR(a.created_at, 'YYYY-MM-DD') as day, COUNT(a.id) as count " +
                    "FROM activity_log a " +
                    "WHERE a.created_at BETWEEN :startDate AND :endDate " +
                    "GROUP BY TO_CHAR(a.created_at, 'YYYY-MM-DD') " +
                    "ORDER BY day",
            nativeQuery = true)
    List<Object[]> countByDayAndCreatedAtBetween(
            @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 사용자 ID로 활동 로그를 조회합니다.
     */
    List<ActivityLog> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    /**
     * 사용자 ID로 활동 로그를 조회합니다. (제한 개수)
     */
    @Query(value = "SELECT * FROM activity_log WHERE user_id = :userId ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<ActivityLog> findByUserIdOrderByCreatedAtDesc(@Param("userId") String userId, @Param("limit") int limit);

    /**
     * 멤버 ID로 활동 로그를 조회합니다.
     */
    List<ActivityLog> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    /**
     * 멤버 ID로 활동 로그를 조회합니다. (제한 개수)
     */
    @Query(value = "SELECT * FROM activity_log WHERE member_id = :memberId ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<ActivityLog> findByMemberIdOrderByCreatedAtDesc(@Param("memberId") Long memberId, @Param("limit") int limit);
}