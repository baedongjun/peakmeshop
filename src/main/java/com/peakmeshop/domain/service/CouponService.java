package com.peakmeshop.domain.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.peakmeshop.api.dto.CouponDTO;
import com.peakmeshop.api.dto.CouponSummaryDTO;
import com.peakmeshop.api.dto.MemberCouponDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponService {

    // 쿠폰 목록 조회
    Page<CouponDTO> getCoupons(String type, String status, String keyword, Pageable pageable);

    // 쿠폰 통계 정보 조회
    CouponSummaryDTO getCouponSummary();

    // 쿠폰 상세 조회
    Optional<CouponDTO> getCoupon(Long id);

    // 쿠폰 생성
    CouponDTO createCoupon(CouponDTO couponDTO);

    // 쿠폰 수정
    CouponDTO updateCoupon(CouponDTO couponDTO);

    // 쿠폰 삭제
    void deleteCoupon(Long id);

    // 쿠폰 발급
    void issueCoupon(Long id);

    // 쿠폰 중지
    void suspendCoupon(Long id);

    // 쿠폰 재개
    void resumeCoupon(Long id);

    // 쿠폰 조회
    CouponDTO getCouponById(Long id);

    // 쿠폰 코드로 조회
    CouponDTO getCouponByCode(String code);

    // 모든 쿠폰 조회 (페이징)
    Page<CouponDTO> getAllCoupons(Pageable pageable);

    // 모든 쿠폰 조회 (리스트)
    List<CouponDTO> getAllCoupons();

    // 활성화된 쿠폰 조회 (페이징)
    Page<CouponDTO> getActiveCoupons(Pageable pageable);

    // 활성화된 쿠폰 조회 (리스트)
    List<CouponDTO> getActiveCoupons();

    // 쿠폰 수정
    CouponDTO updateCoupon(Long id, CouponDTO couponDTO);

    // 쿠폰 활성화/비활성화
    CouponDTO toggleCouponStatus(Long id);

    // 모든 회원에게 쿠폰 발급
    List<MemberCouponDTO> issueCouponToAllMembers(Long couponId);

    // 특정 회원에게 쿠폰 발급
    MemberCouponDTO issueCouponToMember(Long couponId, Long memberId);

    // 쿠폰 코드로 특정 회원에게 쿠폰 발급
    MemberCouponDTO issueCouponByCode(String code, Long memberId);

    // 특정 회원의 쿠폰 목록 조회 (페이징)
    Page<MemberCouponDTO> getMemberCoupons(Long memberId, Pageable pageable);

    // 특정 회원의 쿠폰 목록 조회 (리스트)
    List<MemberCouponDTO> getMemberCoupons(Long memberId);

    // 특정 회원의 사용 가능한 쿠폰 목록 조회 (페이징)
    Page<MemberCouponDTO> getMemberUnusedCoupons(Long memberId, Pageable pageable);

    // 특정 회원의 사용 가능한 쿠폰 목록 조회 (리스트)
    List<MemberCouponDTO> getAvailableMemberCoupons(Long memberId);

    // 회원 쿠폰 ID로 조회
    MemberCouponDTO getMemberCouponById(Long memberCouponId);

    // 회원 쿠폰 사용
    MemberCouponDTO useMemberCoupon(Long memberCouponId);

    // 회원 쿠폰 취소 (다른 이름)
    MemberCouponDTO cancelUsedCoupon(Long memberCouponId);

    // 회원 쿠폰 취소
    MemberCouponDTO cancelMemberCoupon(Long memberCouponId);

    // 쿠폰 코드 유효성 검사
    Map<String, Object> validateCouponCode(String code);

    // 만료된 쿠폰 처리
    void expireCoupons();
}