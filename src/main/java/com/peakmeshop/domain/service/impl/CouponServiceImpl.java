package com.peakmeshop.domain.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.peakmeshop.api.dto.CouponDTO;
import com.peakmeshop.api.dto.CouponSummaryDTO;
import com.peakmeshop.api.dto.MemberCouponDTO;
import com.peakmeshop.domain.entity.Coupon;
import com.peakmeshop.domain.entity.Member;
import com.peakmeshop.domain.entity.MemberCoupon;
import com.peakmeshop.domain.repository.CouponRepository;
import com.peakmeshop.domain.repository.MemberCouponRepository;
import com.peakmeshop.domain.repository.MemberRepository;
import com.peakmeshop.domain.service.CouponService;
import com.peakmeshop.api.mapper.CouponMapper;
import com.peakmeshop.api.mapper.MemberCouponMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final MemberRepository memberRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final CouponMapper couponMapper;
    private final MemberCouponMapper memberCouponMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<CouponDTO> getCoupons(String type, String status, String keyword, Pageable pageable) {
        Page<Coupon> coupons;
        if (keyword != null && !keyword.isEmpty()) {
            coupons = couponRepository.searchCoupons(keyword, pageable);
        } else if (status != null && !status.isEmpty()) {
            coupons = couponRepository.findByStatus(status, pageable);
        } else if (type != null && !type.isEmpty()) {
            coupons = couponRepository.findByDiscountType(type, pageable);
        } else {
            coupons = couponRepository.findAll(pageable);
        }
        return coupons.map(couponMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponSummaryDTO getCouponSummary() {
        long totalCoupons = couponRepository.count();
        long activeCoupons = couponRepository.countByStatus(Coupon.STATUS_ACTIVE);
        long expiredCoupons = couponRepository.countByStatus(Coupon.STATUS_EXPIRED);
        long usedCoupons = couponRepository.countByUsedCountGreaterThan(0);

        return CouponSummaryDTO.builder()
                .totalCoupons(totalCoupons)
                .activeCoupons(activeCoupons)
                .expiredCoupons(expiredCoupons)
                .usedCoupons(usedCoupons)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CouponSummaryDTO getCouponSummary(String period, String startDate, String endDate) {
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (startDate != null && endDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localStartDate = LocalDate.parse(startDate, formatter);
            LocalDate localEndDate = LocalDate.parse(endDate, formatter);
            start = localStartDate.atStartOfDay();
            end = localEndDate.plusDays(1).atStartOfDay();
        } else {
            LocalDateTime now = LocalDateTime.now();
            if (period != null) {
                switch (period) {
                    case "daily":
                        start = now.toLocalDate().atStartOfDay();
                        end = now.plusDays(1).toLocalDate().atStartOfDay();
                        break;
                    case "weekly":
                        start = now.minusWeeks(1).toLocalDate().atStartOfDay();
                        end = now.plusDays(1).toLocalDate().atStartOfDay();
                        break;
                    case "monthly":
                        start = now.minusMonths(1).toLocalDate().atStartOfDay();
                        end = now.plusDays(1).toLocalDate().atStartOfDay();
                        break;
                    case "yearly":
                        start = now.minusYears(1).toLocalDate().atStartOfDay();
                        end = now.plusDays(1).toLocalDate().atStartOfDay();
                        break;
                    default:
                        start = now.minusMonths(1).toLocalDate().atStartOfDay();
                        end = now.plusDays(1).toLocalDate().atStartOfDay();
                }
            } else {
                start = now.minusMonths(1).toLocalDate().atStartOfDay();
                end = now.plusDays(1).toLocalDate().atStartOfDay();
            }
        }

        long totalCoupons = couponRepository.countByCreatedAtBetween(start, end);
        long activeCoupons = couponRepository.countByStatusAndCreatedAtBetween(Coupon.STATUS_ACTIVE, start, end);
        long expiredCoupons = couponRepository.countByStatusAndCreatedAtBetween(Coupon.STATUS_EXPIRED, start, end);
        long usedCoupons = couponRepository.countByUsedCountGreaterThanAndCreatedAtBetween(0, start, end);

        return CouponSummaryDTO.builder()
                .totalCoupons(totalCoupons)
                .activeCoupons(activeCoupons)
                .expiredCoupons(expiredCoupons)
                .usedCoupons(usedCoupons)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CouponDTO> getCoupon(Long id) {
        return couponRepository.findById(id)
                .map(couponMapper::toDTO);
    }

    @Override
    @Transactional
    public CouponDTO createCoupon(CouponDTO couponDTO) {
        if (couponDTO.getCode() == null || couponDTO.getCode().isEmpty()) {
            couponDTO.setCode(generateCouponCode());
        } else {
            if (couponRepository.findByCode(couponDTO.getCode()).isPresent()) {
                throw new IllegalArgumentException("이미 존재하는 쿠폰 코드입니다.");
            }
        }

        Coupon coupon = couponMapper.toEntity(couponDTO);
        coupon.setUsedCount(0);
        coupon.setStatus(Coupon.STATUS_ACTIVE);
        coupon.setCreatedAt(LocalDateTime.now());
        coupon.setUpdatedAt(LocalDateTime.now());

        Coupon savedCoupon = couponRepository.save(coupon);
        return couponMapper.toDTO(savedCoupon);
    }

    @Override
    @Transactional
    public CouponDTO updateCoupon(CouponDTO couponDTO) {
        if (couponDTO.getId() == null) {
            throw new IllegalArgumentException("쿠폰 ID가 필요합니다.");
        }
        return updateCoupon(couponDTO.getId(), couponDTO);
    }

    @Override
    @Transactional
    public void deleteCoupon(Long id) {
        couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
        couponRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void issueCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        if (!Coupon.STATUS_ACTIVE.equals(coupon.getStatus())) {
            coupon.setStatus(Coupon.STATUS_ACTIVE);
            coupon.setUpdatedAt(LocalDateTime.now());
            couponRepository.save(coupon);
        }
    }

    @Override
    @Transactional
    public void suspendCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        if (!Coupon.STATUS_INACTIVE.equals(coupon.getStatus())) {
            coupon.setStatus(Coupon.STATUS_INACTIVE);
            coupon.setUpdatedAt(LocalDateTime.now());
            couponRepository.save(coupon);
        }
    }

    @Override
    @Transactional
    public void resumeCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        if (!Coupon.STATUS_ACTIVE.equals(coupon.getStatus())) {
            coupon.setStatus(Coupon.STATUS_ACTIVE);
            coupon.setUpdatedAt(LocalDateTime.now());
            couponRepository.save(coupon);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CouponDTO getCouponById(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
        return couponMapper.toDTO(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponDTO getCouponByCode(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
        return couponMapper.toDTO(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CouponDTO> getAllCoupons(Pageable pageable) {
        Page<Coupon> couponsPage = couponRepository.findAll(pageable);
        return couponsPage.map(couponMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponDTO> getAllCoupons() {
        List<Coupon> coupons = couponRepository.findAll();
        return coupons.stream()
                .map(couponMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CouponDTO> getActiveCoupons(Pageable pageable) {
        Page<Coupon> couponsPage = couponRepository.findByStatus(Coupon.STATUS_ACTIVE, pageable);
        return couponsPage.map(couponMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponDTO> getActiveCoupons() {
        List<Coupon> coupons = couponRepository.findByStatus(Coupon.STATUS_ACTIVE);
        return coupons.stream()
                .map(couponMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CouponDTO updateCoupon(Long id, CouponDTO couponDTO) {
        Coupon existingCoupon = couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        Coupon updatedCoupon = couponMapper.toEntity(couponDTO);
        existingCoupon.setName(updatedCoupon.getName());
        existingCoupon.setDescription(updatedCoupon.getDescription());
        existingCoupon.setDiscountType(updatedCoupon.getDiscountType());
        existingCoupon.setDiscountValue(updatedCoupon.getDiscountValue());
        existingCoupon.setMinOrderAmount(updatedCoupon.getMinOrderAmount());
        existingCoupon.setMaxDiscountAmount(updatedCoupon.getMaxDiscountAmount());
        existingCoupon.setStartDate(updatedCoupon.getStartDate());
        existingCoupon.setEndDate(updatedCoupon.getEndDate());
        existingCoupon.setUsageLimit(updatedCoupon.getUsageLimit());
        existingCoupon.setUpdatedAt(LocalDateTime.now());

        Coupon savedCoupon = couponRepository.save(existingCoupon);
        return couponMapper.toDTO(savedCoupon);
    }

    @Override
    @Transactional
    public CouponDTO toggleCouponStatus(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        if (Coupon.STATUS_ACTIVE.equals(coupon.getStatus())) {
            coupon.setStatus(Coupon.STATUS_INACTIVE);
        } else {
            coupon.setStatus(Coupon.STATUS_ACTIVE);
        }

        coupon.setUpdatedAt(LocalDateTime.now());
        Coupon updatedCoupon = couponRepository.save(coupon);
        return couponMapper.toDTO(updatedCoupon);
    }

    @Override
    @Transactional
    public List<MemberCouponDTO> issueCouponToAllMembers(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        if (!Coupon.STATUS_ACTIVE.equals(coupon.getStatus())) {
            throw new IllegalArgumentException("비활성화된 쿠폰은 발급할 수 없습니다.");
        }

        List<Member> members = memberRepository.findAll();
        List<MemberCoupon> memberCoupons = members.stream()
                .filter(member -> !memberCouponRepository.existsByMemberIdAndCouponId(member.getId(), couponId))
                .map(member -> {
                    MemberCoupon memberCoupon = MemberCoupon.builder()
                            .member(member)
                            .coupon(coupon)
                            .used(false)
                            .issuedAt(LocalDateTime.now())
                            .build();
                    return memberCouponRepository.save(memberCoupon);
                })
                .collect(Collectors.toList());

        return memberCoupons.stream()
                .map(memberCouponMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MemberCouponDTO issueCouponToMember(Long couponId, Long memberId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        if (!Coupon.STATUS_ACTIVE.equals(coupon.getStatus())) {
            throw new IllegalArgumentException("비활성화된 쿠폰은 발급할 수 없습니다.");
        }

        if (memberCouponRepository.existsByMemberIdAndCouponId(memberId, couponId)) {
            throw new IllegalArgumentException("이미 발급된 쿠폰입니다.");
        }

        MemberCoupon memberCoupon = MemberCoupon.builder()
                .member(member)
                .coupon(coupon)
                .used(false)
                .issuedAt(LocalDateTime.now())
                .build();

        MemberCoupon savedMemberCoupon = memberCouponRepository.save(memberCoupon);
        return memberCouponMapper.toDTO(savedMemberCoupon);
    }

    @Override
    @Transactional
    public MemberCouponDTO issueCouponByCode(String code, Long memberId) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        if (!Coupon.STATUS_ACTIVE.equals(coupon.getStatus())) {
            throw new IllegalArgumentException("비활성화된 쿠폰은 발급할 수 없습니다.");
        }

        if (memberCouponRepository.existsByMemberIdAndCouponId(memberId, coupon.getId())) {
            throw new IllegalArgumentException("이미 발급된 쿠폰입니다.");
        }

        MemberCoupon memberCoupon = MemberCoupon.builder()
                .member(member)
                .coupon(coupon)
                .used(false)
                .issuedAt(LocalDateTime.now())
                .build();

        MemberCoupon savedMemberCoupon = memberCouponRepository.save(memberCoupon);
        return memberCouponMapper.toDTO(savedMemberCoupon);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberCouponDTO> getMemberCoupons(Long memberId, Pageable pageable) {
        Page<MemberCoupon> memberCouponsPage = memberCouponRepository.findByMemberId(memberId, pageable);
        return memberCouponsPage.map(memberCouponMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberCouponDTO> getMemberCoupons(Long memberId) {
        List<MemberCoupon> memberCoupons = memberCouponRepository.findByMemberId(memberId);
        return memberCoupons.stream()
                .map(memberCouponMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberCouponDTO> getMemberUnusedCoupons(Long memberId, Pageable pageable) {
        Page<MemberCoupon> memberCouponsPage = memberCouponRepository.findByMemberIdAndUsedFalse(memberId, pageable);
        return memberCouponsPage.map(memberCouponMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberCouponDTO> getAvailableMemberCoupons(Long memberId) {
        LocalDateTime now = LocalDateTime.now();
        List<MemberCoupon> memberCoupons = memberCouponRepository.findByMemberIdAndUsedFalseAndCouponStartDateBeforeAndCouponEndDateAfter(
                memberId, now);
        return memberCoupons.stream()
                .map(memberCouponMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MemberCouponDTO getMemberCouponById(Long memberCouponId) {
        MemberCoupon memberCoupon = memberCouponRepository.findById(memberCouponId)
                .orElseThrow(() -> new IllegalArgumentException("회원 쿠폰을 찾을 수 없습니다."));
        return memberCouponMapper.toDTO(memberCoupon);
    }

    @Override
    @Transactional
    public MemberCouponDTO useMemberCoupon(Long memberCouponId) {
        MemberCoupon memberCoupon = memberCouponRepository.findById(memberCouponId)
                .orElseThrow(() -> new IllegalArgumentException("회원 쿠폰을 찾을 수 없습니다."));

        if (memberCoupon.isUsed()) {
            throw new IllegalArgumentException("이미 사용된 쿠폰입니다.");
        }

        if (!Coupon.STATUS_ACTIVE.equals(memberCoupon.getCoupon().getStatus())) {
            throw new IllegalArgumentException("비활성화된 쿠폰입니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (memberCoupon.getCoupon().getEndDate() != null && memberCoupon.getCoupon().getEndDate().isBefore(now)) {
            throw new IllegalArgumentException("만료된 쿠폰입니다.");
        }

        memberCoupon.setUsed(true);
        memberCoupon.setUsedAt(now);

        // 쿠폰 사용 횟수 증가
        Coupon coupon = memberCoupon.getCoupon();
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);

        MemberCoupon updatedMemberCoupon = memberCouponRepository.save(memberCoupon);
        return memberCouponMapper.toDTO(updatedMemberCoupon);
    }

    @Override
    @Transactional
    public MemberCouponDTO cancelUsedCoupon(Long memberCouponId) {
        return cancelMemberCoupon(memberCouponId);
    }

    @Override
    @Transactional
    public MemberCouponDTO cancelMemberCoupon(Long memberCouponId) {
        MemberCoupon memberCoupon = memberCouponRepository.findById(memberCouponId)
                .orElseThrow(() -> new IllegalArgumentException("회원 쿠폰을 찾을 수 없습니다."));

        if (!memberCoupon.isUsed()) {
            throw new IllegalArgumentException("사용되지 않은 쿠폰입니다.");
        }

        memberCoupon.setUsed(false);
        memberCoupon.setUsedAt(null);

        // 쿠폰 사용 횟수 감소
        Coupon coupon = memberCoupon.getCoupon();
        coupon.setUsedCount(coupon.getUsedCount() - 1);
        couponRepository.save(coupon);

        MemberCoupon updatedMemberCoupon = memberCouponRepository.save(memberCoupon);
        return memberCouponMapper.toDTO(updatedMemberCoupon);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> validateCouponCode(String code) {
        Map<String, Object> result = new HashMap<>();

        try {
            Coupon coupon = couponRepository.findByCode(code)
                    .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

            LocalDateTime now = LocalDateTime.now();
            boolean isValid = true;
            String message = "유효한 쿠폰입니다.";

            // 쿠폰 상태 확인
            if (!Coupon.STATUS_ACTIVE.equals(coupon.getStatus())) {
                isValid = false;
                message = "비활성화된 쿠폰입니다.";
            }

            // 쿠폰 기간 확인
            else if (coupon.getStartDate() != null && coupon.getStartDate().isAfter(now)) {
                isValid = false;
                message = "아직 사용 기간이 시작되지 않은 쿠폰입니다.";
            }

            else if (coupon.getEndDate() != null && coupon.getEndDate().isBefore(now)) {
                isValid = false;
                message = "만료된 쿠폰입니다.";
            }

            // 사용 제한 확인
            else if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
                isValid = false;
                message = "사용 가능 횟수를 초과한 쿠폰입니다.";
            }

            result.put("valid", isValid);
            result.put("message", message);

            if (isValid) {
                result.put("coupon", couponMapper.toDTO(coupon));
            }

            return result;
        } catch (IllegalArgumentException e) {
            result.put("valid", false);
            result.put("message", "존재하지 않는 쿠폰 코드입니다.");
            return result;
        } catch (Exception e) {
            result.put("valid", false);
            result.put("message", "쿠폰 검증 중 오류가 발생했습니다.");
            return result;
        }
    }

    @Override
    @Transactional
    public void expireCoupons() {
        LocalDateTime now = LocalDateTime.now();

        // 만료된 쿠폰 처리
        List<Coupon> expiredCoupons = couponRepository.findByEndDateBeforeAndStatusNot(now, Coupon.STATUS_EXPIRED);
        for (Coupon coupon : expiredCoupons) {
            coupon.setStatus(Coupon.STATUS_EXPIRED);
            coupon.setUpdatedAt(now);
            couponRepository.save(coupon);
        }
    }

    private String generateCouponCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}