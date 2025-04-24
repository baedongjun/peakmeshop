package com.peakmeshop.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peakmeshop.domain.entity.MemberCoupon;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {

    List<MemberCoupon> findByMemberId(Long memberId);

    Page<MemberCoupon> findByMemberId(Long memberId, Pageable pageable);

    Optional<MemberCoupon> findByMemberIdAndCouponId(Long memberId, Long couponId);

    boolean existsByMemberIdAndCouponId(Long memberId, Long couponId);

    @Query("SELECT mc FROM MemberCoupon mc WHERE mc.member.id = :memberId AND mc.used = false")
    Page<MemberCoupon> findByMemberIdAndUsedFalse(@Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT mc FROM MemberCoupon mc WHERE mc.member.id = :memberId AND mc.used = false " +
           "AND mc.coupon.startDate <= :now AND mc.coupon.endDate >= :now")
    List<MemberCoupon> findByMemberIdAndUsedFalseAndCouponStartDateBeforeAndCouponEndDateAfter(
            @Param("memberId") Long memberId,
            @Param("now") LocalDateTime now);

    List<MemberCoupon> findByEndDateBeforeAndUsedFalse(LocalDateTime date);
}