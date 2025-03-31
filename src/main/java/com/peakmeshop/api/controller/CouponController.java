package com.peakmeshop.api.controller;

import com.peakmeshop.api.dto.CouponDTO;
import com.peakmeshop.api.dto.MemberCouponDTO;
import com.peakmeshop.common.security.oauth2.user.UserPrincipal;
import com.peakmeshop.domain.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<CouponDTO> createCoupon(@RequestBody CouponDTO couponDTO) {
        CouponDTO createdCoupon = couponService.createCoupon(couponDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCoupon);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CouponDTO> getCouponById(@PathVariable Long id) {
        CouponDTO coupon = couponService.getCouponById(id);
        return ResponseEntity.ok(coupon);
    }

    @GetMapping
    public ResponseEntity<Page<CouponDTO>> getAllCoupons(Pageable pageable) {
        Page<CouponDTO> coupons = couponService.getAllCoupons(pageable);
        return ResponseEntity.ok(coupons);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CouponDTO>> getAllCouponsWithoutPaging() {
        List<CouponDTO> coupons = couponService.getAllCoupons();
        return ResponseEntity.ok(coupons);
    }

    @GetMapping("/active")
    public ResponseEntity<Page<CouponDTO>> getActiveCoupons(Pageable pageable) {
        Page<CouponDTO> coupons = couponService.getActiveCoupons(pageable);
        return ResponseEntity.ok(coupons);
    }

    @GetMapping("/active/all")
    public ResponseEntity<List<CouponDTO>> getActiveCouponsWithoutPaging() {
        List<CouponDTO> coupons = couponService.getActiveCoupons();
        return ResponseEntity.ok(coupons);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CouponDTO> updateCoupon(
            @PathVariable Long id, @RequestBody CouponDTO couponDTO) {
        CouponDTO updatedCoupon = couponService.updateCoupon(id, couponDTO);
        return ResponseEntity.ok(updatedCoupon);
    }

    @PutMapping
    public ResponseEntity<CouponDTO> updateCouponWithId(@RequestBody CouponDTO couponDTO) {
        CouponDTO updatedCoupon = couponService.updateCoupon(couponDTO);
        return ResponseEntity.ok(updatedCoupon);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{couponId}/issue/{memberId}")
    public ResponseEntity<MemberCouponDTO> issueCouponToMember(
            @PathVariable Long couponId, @PathVariable Long memberId) {
        MemberCouponDTO memberCoupon = couponService.issueCouponToMember(couponId, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(memberCoupon);
    }

    @PostMapping("/{couponId}/issue-all")
    public ResponseEntity<List<MemberCouponDTO>> issueCouponToAllMembers(@PathVariable Long couponId) {
        List<MemberCouponDTO> memberCoupons = couponService.issueCouponToAllMembers(couponId);
        return ResponseEntity.status(HttpStatus.CREATED).body(memberCoupons);
    }

    @PostMapping("/issue-by-code")
    public ResponseEntity<MemberCouponDTO> issueCouponByCode(
            @RequestParam String code,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        MemberCouponDTO memberCoupon = couponService.issueCouponByCode(code, userPrincipal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(memberCoupon);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<Page<MemberCouponDTO>> getMemberCoupons(
            @PathVariable Long memberId, Pageable pageable) {
        Page<MemberCouponDTO> memberCoupons = couponService.getMemberCoupons(memberId, pageable);
        return ResponseEntity.ok(memberCoupons);
    }

    @GetMapping("/member/{memberId}/unused")
    public ResponseEntity<Page<MemberCouponDTO>> getMemberUnusedCoupons(
            @PathVariable Long memberId, Pageable pageable) {
        Page<MemberCouponDTO> memberCoupons = couponService.getMemberUnusedCoupons(memberId, pageable);
        return ResponseEntity.ok(memberCoupons);
    }

    @GetMapping("/member-coupon/{id}")
    public ResponseEntity<MemberCouponDTO> getMemberCouponById(@PathVariable Long id) {
        MemberCouponDTO memberCoupon = couponService.getMemberCouponById(id);
        return ResponseEntity.ok(memberCoupon);
    }

    @PostMapping("/member-coupon/{id}/use")
    public ResponseEntity<MemberCouponDTO> useMemberCoupon(@PathVariable Long id) {
        MemberCouponDTO memberCoupon = couponService.useMemberCoupon(id);
        return ResponseEntity.ok(memberCoupon);
    }

    @PostMapping("/member-coupon/{id}/cancel")
    public ResponseEntity<MemberCouponDTO> cancelUsedCoupon(@PathVariable Long id) {
        MemberCouponDTO memberCoupon = couponService.cancelUsedCoupon(id);
        return ResponseEntity.ok(memberCoupon);
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateCouponCode(@RequestParam String code) {
        Map<String, Object> result = couponService.validateCouponCode(code);
        return ResponseEntity.ok(result);
    }
}