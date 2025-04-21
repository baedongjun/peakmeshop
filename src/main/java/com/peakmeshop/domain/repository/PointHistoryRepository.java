package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.PointHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    Page<PointHistory> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(ph.points), 0) FROM PointHistory ph " +
           "WHERE ph.memberId = :memberId AND ph.expiryDate > CURRENT_TIMESTAMP " +
           "AND ph.points > 0")
    Integer findExpiringPoints(@Param("memberId") Long memberId);

    @Query("SELECT ph FROM PointHistory ph " +
           "WHERE ph.expiryDate BETWEEN :start AND :end " +
           "AND ph.points > 0")
    List<PointHistory> findExpiringPoints(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}