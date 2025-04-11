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

    // 특정 기간 동안 가입한 회원 수 계산
    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    Page<Member> findByIsWithdrawnTrue(Pageable pageable);

    Page<Member> findByIsWithdrawnTrueAndWithdrawnAtBetween(
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<Member> findByLastLoginAtBeforeAndIsWithdrawnFalse(LocalDateTime date, Pageable pageable);

    List<Member> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    long countByStatus(String status);

    @Query("SELECT m.status, COUNT(m) FROM Member m GROUP BY m.status")
    List<Object[]> findMemberStatusDistribution();

    @Query("SELECT cast(m.createdAt as date) as date, COUNT(m) as count " +
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
}