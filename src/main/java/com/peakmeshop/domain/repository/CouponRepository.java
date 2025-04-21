package com.peakmeshop.domain.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.peakmeshop.domain.entity.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode(String code);

    List<Coupon> findByStatus(String status);

    Page<Coupon> findByStatus(String status, Pageable pageable);

    List<Coupon> findByEndDateBeforeAndStatusNot(LocalDateTime date, String status);

    // 쿠폰 검색
    @Query("SELECT c FROM Coupon c WHERE " +
            "LOWER(c.name) LIKE %:keyword% OR " +
            "LOWER(c.description) LIKE %:keyword% OR " +
            "LOWER(c.code) LIKE %:keyword%")
    Page<Coupon> searchCoupons(@Param("keyword") String keyword, Pageable pageable);

    // 할인 유형별 쿠폰 조회
    Page<Coupon> findByDiscountType(String discountType, Pageable pageable);

    // 상태별 쿠폰 개수 조회
    long countByStatus(String status);

    // 사용 횟수가 특정 값보다 큰 쿠폰 개수 조회
    long countByUsedCountGreaterThan(int count);

    // 생성일 기간 내 쿠폰 개수 조회
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // 상태 및 생성일 기간 내 쿠폰 개수 조회
    long countByStatusAndCreatedAtBetween(String status, LocalDateTime start, LocalDateTime end);

    // 사용 횟수 및 생성일 기간 내 쿠폰 개수 조회
    long countByUsedCountGreaterThanAndCreatedAtBetween(int count, LocalDateTime start, LocalDateTime end);

    // 만료일이 특정 날짜 이전인 쿠폰 조회
    List<Coupon> findByEndDateBefore(LocalDateTime date);

    // 시작일이 특정 날짜 이후인 쿠폰 조회
    List<Coupon> findByStartDateAfter(LocalDateTime date);

    // 활성 상태이고 시작일과 종료일 사이에 있는 쿠폰 조회
    List<Coupon> findByStatusAndStartDateBeforeAndEndDateAfter(
            String status, LocalDateTime currentDate, LocalDateTime currentDate2);

    // 활성 상태이고 시작일과 종료일 사이에 있는 쿠폰 조회 (페이징)
    Page<Coupon> findByStatusAndStartDateBeforeAndEndDateAfter(
            String status, LocalDateTime currentDate, LocalDateTime currentDate2, Pageable pageable);

    @Query("SELECT c FROM Coupon c JOIN c.memberCoupons mc " +
           "WHERE mc.member.id = :memberId " +
           "AND c.startDate <= :now " +
           "AND c.endDate > :now " +
           "AND mc.used = false")
    List<Coupon> findAvailableCouponsByMemberId(@Param("memberId") Long memberId, 
                                               @Param("now") LocalDateTime now);

    @Query("SELECT c FROM Coupon c JOIN c.memberCoupons mc " +
           "WHERE mc.member.id = :memberId " +
           "AND (:expired = true AND c.endDate <= :now OR " +
           "     :expired = false AND c.endDate > :now)")
    List<Coupon> findExpiredCouponsByMemberId(@Param("memberId") Long memberId,
                                             @Param("expired") boolean expired,
                                             @Param("now") LocalDateTime now);

    @Query("SELECT c FROM Coupon c JOIN c.memberCoupons mc " +
           "WHERE mc.member.id = :memberId " +
           "AND c.startDate <= :now " +
           "AND c.endDate > :now " +
           "AND mc.used = false " +
           "AND c.minOrderAmount <= :totalAmount")
    List<Coupon> findAvailableCoupons(@Param("memberId") Long memberId,
                                     @Param("totalAmount") BigDecimal totalAmount,
                                     @Param("now") LocalDateTime now);

    @Query("SELECT c FROM Coupon c WHERE " +
           "(:type IS NULL OR c.type = :type) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:keyword IS NULL OR c.name LIKE %:keyword% OR c.code LIKE %:keyword%)")
    Page<Coupon> findCoupons(@Param("type") String type,
                            @Param("status") String status,
                            @Param("keyword") String keyword,
                            Pageable pageable);

}