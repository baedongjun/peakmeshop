package com.peakmeshop.domain.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.math.BigDecimal;

import com.peakmeshop.api.dto.CouponDTO;
import com.peakmeshop.api.dto.CouponSummaryDTO;
import com.peakmeshop.api.dto.MemberCouponDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponService {
    
    Page<CouponDTO> getCoupons(String type, String status, String keyword, Pageable pageable);
    
    CouponSummaryDTO getCouponSummary();
    
    CouponSummaryDTO getCouponSummary(String period, String startDate, String endDate);
    
    Optional<CouponDTO> getCoupon(Long id);
    
    CouponDTO createCoupon(CouponDTO couponDTO);
    
    CouponDTO updateCoupon(CouponDTO couponDTO);
    
    void deleteCoupon(Long id);
    
    void issueCoupon(Long id);
    
    void suspendCoupon(Long id);
    
    void resumeCoupon(Long id);
    
    CouponDTO getCouponById(Long id);
    
    CouponDTO getCouponByCode(String code);
    
    Page<CouponDTO> getAllCoupons(Pageable pageable);
    
    List<CouponDTO> getAllCoupons();
    
    Page<CouponDTO> getActiveCoupons(Pageable pageable);
    
    List<CouponDTO> getActiveCoupons();
    
    CouponDTO updateCoupon(Long id, CouponDTO couponDTO);
    
    CouponDTO toggleCouponStatus(Long id);
    
    List<MemberCouponDTO> issueCouponToAllMembers(Long couponId);
    
    MemberCouponDTO issueCouponToMember(Long couponId, Long memberId);
    
    MemberCouponDTO issueCouponByCode(String code, Long memberId);
    
    Page<MemberCouponDTO> getMemberCoupons(Long memberId, Pageable pageable);
    
    List<MemberCouponDTO> getAvailableMemberCoupons(Long memberId);
    
    MemberCouponDTO getMemberCouponById(Long memberCouponId);
    
    Map<String, Object> validateCouponCode(String code);
    
    void expireCoupons();
    
    List<CouponDTO> getCouponsByUserId(String userId);
    
    List<CouponDTO> getCouponsByUserId(String userId, Boolean expired);
}