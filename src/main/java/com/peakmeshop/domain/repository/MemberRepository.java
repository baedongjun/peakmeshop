package com.peakmeshop.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.peakmeshop.domain.entity.Member;
import com.peakmeshop.domain.entity.MemberGrade;
import com.peakmeshop.api.dto.StatisticsDTO;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByUserId(String userId);

    Optional<Member> findByVerificationToken(String token);

    Optional<Member> findByResetToken(String token);

    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);

    List<Member> findByEmailContainingOrNameContaining(String email, String name);

    Page<Member> findByEmailContainingOrNameContaining(String email, String name, Pageable pageable);

    Page<Member> findByIsWithdrawnTrue(Pageable pageable);

    Page<Member> findByIsWithdrawnTrueAndWithdrawnAtBetween(
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<Member> findByLastLoginAtBeforeAndIsWithdrawnFalse(LocalDateTime date, Pageable pageable);

    List<Member> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    long countByStatus(String status);

    @Query("SELECT m.status, COUNT(m) FROM Member m GROUP BY m.status")
    List<Object[]> findMemberStatusDistribution();

    @Query("SELECT date(m.createdAt) as date, COUNT(m) as count " +
            "FROM Member m " +
            "WHERE m.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY cast(m.createdAt as date) " +
            "ORDER BY date")
    List<Object[]> findDailySignups(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);

    @Query("SELECT to_char(m.createdAt, 'YYYY-MM') as month, " +
            "COUNT(m) as count " +
            "FROM Member m " +
            "WHERE m.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY to_char(m.createdAt, 'YYYY-MM') " +
            "ORDER BY month")
    List<Object[]> findMonthlySignups(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT m FROM Member m WHERE m.lastLoginAt < :threshold AND m.isWithdrawn = false")
    Page<Member> findDormantMembers(@Param("threshold") LocalDateTime threshold, Pageable pageable);

    @Query("SELECT g FROM MemberGrade g WHERE g.isActive = true")
    List<MemberGrade> findAllGrades();

    @Query("SELECT g FROM MemberGrade g WHERE g.id = :id")
    Optional<MemberGrade> findGradeById(@Param("id") Long id);

    @Query("SELECT COUNT(m) FROM Member m WHERE m.createdAt BETWEEN :start AND :end")
    long countByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(m) FROM Member m WHERE m.status != m.originalStatus AND m.updatedAt BETWEEN :start AND :end")
    long countStatusChangesBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT m FROM Member m WHERE m.status = 'ACTIVE' AND (m.userId LIKE %:keyword% OR m.name LIKE %:keyword% OR m.email LIKE %:keyword%)")
    Page<Member> findActiveMembersByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT " +
           "m.id, m.email, m.name, cast(m.createdAt as date), " +
           "m.orderCount, m.totalOrderAmount, m.totalPoints, m.status " +
           "FROM Member m " +
           "WHERE m.status = 'ACTIVE' AND m.createdAt BETWEEN :startDateTime AND :endDateTime")
    List<StatisticsDTO.Member> findActiveMembers(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);
}