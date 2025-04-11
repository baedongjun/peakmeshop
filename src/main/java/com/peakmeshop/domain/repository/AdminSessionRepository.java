package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.AdminSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminSessionRepository extends JpaRepository<AdminSession, String> {
    /**
     * 관리자 ID로 세션을 찾습니다.
     * @param adminId 관리자 ID
     * @return 세션 목록
     */
    List<AdminSession> findByAdminId(Long adminId);

    /**
     * 멤버 사용자 ID로 세션을 찾습니다.
     * @param userId 사용자 ID
     * @return 세션 목록
     */
    @Query("SELECT s FROM AdminSession s JOIN s.admin a JOIN a.member m WHERE m.userId = :userId")
    List<AdminSession> findByMemberUserId(@Param("userId") String userId);

    /**
     * 만료되지 않은 관리자 세션을 찾습니다.
     * @param adminId 관리자 ID
     * @param now 현재 시간
     * @return 세션 목록
     */
    @Query("SELECT s FROM AdminSession s WHERE s.admin.id = :adminId AND (s.expiredAt IS NULL OR s.expiredAt > :now)")
    List<AdminSession> findActiveSessionsByAdminId(@Param("adminId") Long adminId, @Param("now") LocalDateTime now);

    /**
     * 특정 세션을 제외한 관리자의 모든 세션을 만료 처리합니다.
     * @param adminId 관리자 ID
     * @param sessionId 제외할 세션 ID
     * @param expiredAt 만료 시간
     * @return 영향받은 행 수
     */
    @Modifying
    @Query("UPDATE AdminSession s SET s.expiredAt = :expiredAt WHERE s.admin.id = :adminId AND s.sessionId != :sessionId")
    int expireAllSessionsExcept(@Param("adminId") Long adminId, @Param("sessionId") Long sessionId, @Param("expiredAt") LocalDateTime expiredAt);

    /**
     * 특정 세션을 만료 처리합니다.
     * @param sessionId 세션 ID
     * @param expiredAt 만료 시간
     * @return 영향받은 행 수
     */
    @Modifying
    @Query("UPDATE AdminSession s SET s.expiredAt = :expiredAt WHERE s.sessionId = :sessionId")
    int expireSession(@Param("sessionId") Long sessionId, @Param("expiredAt") LocalDateTime expiredAt);

    /**
     * 세션 ID로 세션과 관리자, 멤버 정보를 함께 조회합니다.
     * @param sessionId 세션 ID
     * @return 세션 정보 (관리자, 멤버 정보 포함)
     */
    @Query("SELECT s FROM AdminSession s JOIN FETCH s.admin a JOIN FETCH a.member WHERE s.sessionId = :sessionId")
    Optional<AdminSession> findBySessionIdWithAdminAndMember(@Param("sessionId") Long sessionId);

    /**
     * 특정 IP 주소로 생성된 세션을 찾습니다.
     * @param ipAddress IP 주소
     * @return 세션 목록
     */
    List<AdminSession> findByIpAddress(String ipAddress);

    /**
     * 특정 기간 내에 생성된 세션을 찾습니다.
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 세션 목록
     */
    List<AdminSession> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 특정 기간 내에 만료된 세션을 찾습니다.
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 세션 목록
     */
    List<AdminSession> findByExpiredAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 특정 관리자의 만료된 세션을 모두 삭제합니다.
     * @param adminId 관리자 ID
     * @param now 현재 시간
     * @return 영향받은 행 수
     */
    @Modifying
    @Query("DELETE FROM AdminSession s WHERE s.admin.id = :adminId AND s.expiredAt < :now")
    int deleteExpiredSessionsByAdminId(@Param("adminId") Long adminId, @Param("now") LocalDateTime now);

    /**
     * 특정 기간보다 오래된 만료된 세션을 모두 삭제합니다.
     * @param expirationDate 만료 기준 날짜
     * @return 영향받은 행 수
     */
    @Modifying
    @Query("DELETE FROM AdminSession s WHERE s.expiredAt < :expirationDate")
    int deleteAllExpiredSessionsBefore(@Param("expirationDate") LocalDateTime expirationDate);
}