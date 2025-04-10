package com.peakmeshop.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.peakmeshop.domain.entity.Point;

import java.time.LocalDateTime;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {

    Optional<Point> findByMemberId(Long memberId);

    // 회원별 포인트 내역 조회
    Page<Point> findByMemberId(Long memberId, Pageable pageable);
    
    // 회원별 유효한 포인트 내역 조회
    @Query("SELECT p FROM Point p WHERE p.member = :memberId AND p.expiryDate > :now")
    List<Point> findValidPointsByMemberId(@Param("memberId") Long memberId, @Param("now") LocalDateTime now);
    
    // 회원별 총 포인트 조회
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Point p WHERE p.member = :memberId AND p.expiryDate > :now")
    int getTotalPointsByMemberId(@Param("memberId") Long memberId, @Param("now") LocalDateTime now);
    
    // 기간별 포인트 통계
    @Query("SELECT DATE(p.createdAt) as date, COUNT(p) as count, SUM(p.amount) as total " +
           "FROM Point p " +
           "WHERE p.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(p.createdAt)")
    List<Object[]> getPointStatistics(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);
    
    // 만료 예정 포인트 조회
    @Query("SELECT p FROM Point p WHERE p.expiryDate BETWEEN :start AND :end AND p.amount > 0")
    List<Point> findExpiringPoints(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // 포인트 유형별 통계
    @Query("SELECT p.type, COUNT(p), SUM(p.amount) FROM Point p GROUP BY p.type")
    List<Object[]> getPointStatisticsByType();
    
    // 특정 기간 내 적립/사용된 포인트 총액
    @Query("SELECT SUM(CASE WHEN p.amount > 0 THEN p.amount ELSE 0 END) as earned, " +
           "SUM(CASE WHEN p.amount < 0 THEN ABS(p.amount) ELSE 0 END) as used " +
           "FROM Point p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    Object[] getEarnedAndUsedPoints(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);

    /**
     * 전체 유효한 포인트 합계 계산
     * @return 전체 유효한 포인트 합계
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Point p WHERE p.expiryDate > :now")
    int calculateTotalPoints(@Param("now") LocalDateTime now);

    /**
     * 특정 기간의 전체 포인트 합계 계산
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 해당 기간의 포인트 합계
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Point p " +
           "WHERE p.createdAt BETWEEN :startDate AND :endDate")
    int calculateTotalPointsByPeriod(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);

    /**
     * 유형별 포인트 합계 계산
     * @param type 포인트 유형 (EARN, USE, REFUND, EXPIRED)
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 해당 유형의 포인트 합계
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Point p " +
           "WHERE p.type = :type AND p.createdAt BETWEEN :startDate AND :endDate")
    int calculateTotalPointsByType(@Param("type") String type,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);
}