package com.peakmeshop.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.dto.CouponDTO;
import com.peakmeshop.dto.MemberCouponDTO;

public interface CouponService {

    /**
     * 쿠폰 생성 (관리자용)
     * @param couponDTO 쿠폰 정보
     * @return 생성된 쿠폰
     */
    CouponDTO createCoupon(CouponDTO couponDTO);

    /**
     * 쿠폰 수정 (관리자용)
     * @param id 쿠폰 ID
     * @param couponDTO 쿠폰 정보
     * @return 수정된 쿠폰
     */
    CouponDTO updateCoupon(Long id, CouponDTO couponDTO);

    /**
     * 쿠폰 활성화/비활성화 (관리자용)
     * @param id 쿠폰 ID
     * @param isActive 활성화 여부
     * @return 수정된 쿠폰
     */
    CouponDTO toggleCouponStatus(Long id, Boolean isActive);

    /**
     * 쿠폰 ID로 조회
     * @param id 쿠폰 ID
     * @return 쿠폰 정보
     */
    Optional<CouponDTO> getCouponById(Long id);

    /**
     * 쿠폰 코드로 조회
     * @param code 쿠폰 코드
     * @return 쿠폰 정보
     */
    Optional<CouponDTO> getCouponByCode(String code);

    /**
     * 모든 쿠폰 목록 조회 (관리자용)
     * @param pageable 페이징 정보
     * @return 쿠폰 목록
     */
    Page<CouponDTO> getAllCoupons(Pageable pageable);

    /**
     * 회원에게 쿠폰 발급
     * @param memberId 회원 ID
     * @param couponId 쿠폰 ID
     * @return 발급된 회원 쿠폰
     */
    MemberCouponDTO issueCouponToMember(Long memberId, Long couponId);

    /**
     * 쿠폰 코드로 회원에게 쿠폰 발급
     * @param memberId 회원 ID
     * @param couponCode 쿠폰 코드
     * @return 발급된 회원 쿠폰
     */
    MemberCouponDTO issueCouponToMemberByCode(Long memberId, String couponCode);

    /**
     * 회원의 사용 가능한 쿠폰 목록 조회
     * @param memberId 회원 ID
     * @return 쿠폰 목록
     */
    List<MemberCouponDTO> getAvailableCoupons(Long memberId);

    /**
     * 회원의 모든 쿠폰 목록 조회
     * @param memberId 회원 ID
     * @param pageable 페이징 정보
     * @return 쿠폰 목록
     */
    Page<MemberCouponDTO> getMemberCoupons(Long memberId, Pageable pageable);

    /**
     * 쿠폰 사용
     * @param memberId 회원 ID
     * @param memberCouponId 회원 쿠폰 ID
     * @param orderId 주문 ID
     */
    void useCoupon(Long memberId, Long memberCouponId, Long orderId);

    /**
     * 쿠폰 할인 금액 계산
     * @param couponId 쿠폰 ID
     * @param orderAmount 주문 금액
     * @return 할인 금액
     */
    BigDecimal calculateDiscountAmount(Long couponId, BigDecimal orderAmount);
}