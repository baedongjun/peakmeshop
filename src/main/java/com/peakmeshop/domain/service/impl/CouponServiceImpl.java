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
        return coupons.map(this::convertToDTO);
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
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public CouponDTO createCoupon(CouponDTO couponDTO) {
        // 쿠폰 코드 생성 (없는 경우)
        if (couponDTO.getCode() == null || couponDTO.getCode().isEmpty()) {
            couponDTO.setCode(generateCouponCode());
        } else {
            // 쿠폰 코드 중복 확인
            if (couponRepository.findByCode(couponDTO.getCode()).isPresent()) {
                throw new IllegalArgumentException("이미 존재하는 쿠폰 코드입니다.");
            }
        }

        Coupon coupon = Coupon.builder()
                .code(couponDTO.getCode())
                .name(couponDTO.getName())
                .description(couponDTO.getDescription())
                .discountType(couponDTO.getDiscountType())
                .discountValue(couponDTO.getDiscountValue())
                .minOrderAmount(couponDTO.getMinOrderAmount())
                .maxDiscountAmount(couponDTO.getMaxDiscountAmount())
                .startDate(couponDTO.getStartDate())
                .endDate(couponDTO.getEndDate())
                .usageLimit(couponDTO.getUsageLimit())
                .usedCount(0)
                .status(Coupon.STATUS_ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Coupon savedCoupon = couponRepository.save(coupon);
        return convertToDTO(savedCoupon);
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
        return convertToDTO(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponDTO getCouponByCode(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
        return convertToDTO(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CouponDTO> getAllCoupons(Pageable pageable) {
        Page<Coupon> couponsPage = couponRepository.findAll(pageable);
        List<CouponDTO> couponDTOs = couponsPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(couponDTOs, pageable, couponsPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponDTO> getAllCoupons() {
        List<Coupon> coupons = couponRepository.findAll();
        return coupons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CouponDTO> getActiveCoupons(Pageable pageable) {
        Page<Coupon> couponsPage = couponRepository.findByStatus(Coupon.STATUS_ACTIVE, pageable);
        List<CouponDTO> couponDTOs = couponsPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(couponDTOs, pageable, couponsPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponDTO> getActiveCoupons() {
        List<Coupon> coupons = couponRepository.findByStatus(Coupon.STATUS_ACTIVE);
        return coupons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CouponDTO updateCoupon(Long id, CouponDTO couponDTO) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        // 쿠폰 코드 중복 확인 (변경된 경우)
        if (couponDTO.getCode() != null && !couponDTO.getCode().equals(coupon.getCode())) {
            if (couponRepository.findByCode(couponDTO.getCode()).isPresent()) {
                throw new IllegalArgumentException("이미 존재하는 쿠폰 코드입니다.");
            }
            coupon.setCode(couponDTO.getCode());
        }

        if (couponDTO.getName() != null) {
            coupon.setName(couponDTO.getName());
        }
        if (couponDTO.getDescription() != null) {
            coupon.setDescription(couponDTO.getDescription());
        }
        if (couponDTO.getDiscountType() != null) {
            coupon.setDiscountType(couponDTO.getDiscountType());
        }
        if (couponDTO.getDiscountValue() != null) {
            coupon.setDiscountValue(couponDTO.getDiscountValue());
        }
        if (couponDTO.getMinOrderAmount() != null) {
            coupon.setMinOrderAmount(couponDTO.getMinOrderAmount());
        }
        if (couponDTO.getMaxDiscountAmount() != null) {
            coupon.setMaxDiscountAmount(couponDTO.getMaxDiscountAmount());
        }
        if (couponDTO.getStartDate() != null) {
            coupon.setStartDate(couponDTO.getStartDate());
        }
        if (couponDTO.getEndDate() != null) {
            coupon.setEndDate(couponDTO.getEndDate());
        }
        if (couponDTO.getUsageLimit() != null) {
            coupon.setUsageLimit(couponDTO.getUsageLimit());
        }
        if (couponDTO.getStatus() != null) {
            coupon.setStatus(couponDTO.getStatus());
        }

        coupon.setUpdatedAt(LocalDateTime.now());
        Coupon updatedCoupon = couponRepository.save(coupon);
        return convertToDTO(updatedCoupon);
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
        return convertToDTO(updatedCoupon);
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
                .map(this::convertToMemberCouponDTO)
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
        return convertToMemberCouponDTO(savedMemberCoupon);
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
        return convertToMemberCouponDTO(savedMemberCoupon);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberCouponDTO> getMemberCoupons(Long memberId, Pageable pageable) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        Page<MemberCoupon> memberCouponsPage = memberCouponRepository.findByMemberId(memberId, pageable);
        List<MemberCouponDTO> memberCouponDTOs = memberCouponsPage.getContent().stream()
                .map(this::convertToMemberCouponDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(memberCouponDTOs, pageable, memberCouponsPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberCouponDTO> getMemberCoupons(Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        List<MemberCoupon> memberCoupons = memberCouponRepository.findByMemberId(memberId);
        return memberCoupons.stream()
                .map(this::convertToMemberCouponDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberCouponDTO> getMemberUnusedCoupons(Long memberId, Pageable pageable) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        LocalDateTime now = LocalDateTime.now();
        Page<MemberCoupon> memberCouponsPage = memberCouponRepository.findByMemberIdAndUsedFalseAndCouponStatusAndCouponEndDateAfter(
                memberId, Coupon.STATUS_ACTIVE, now, pageable);

        List<MemberCouponDTO> memberCouponDTOs = memberCouponsPage.getContent().stream()
                .map(this::convertToMemberCouponDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(memberCouponDTOs, pageable, memberCouponsPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberCouponDTO> getAvailableMemberCoupons(Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        LocalDateTime now = LocalDateTime.now();
        List<MemberCoupon> memberCoupons = memberCouponRepository.findByMemberIdAndUsedFalseAndCouponStatusAndCouponEndDateAfter(
                memberId, Coupon.STATUS_ACTIVE, now);

        return memberCoupons.stream()
                .map(this::convertToMemberCouponDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MemberCouponDTO getMemberCouponById(Long memberCouponId) {
        MemberCoupon memberCoupon = memberCouponRepository.findById(memberCouponId)
                .orElseThrow(() -> new IllegalArgumentException("회원 쿠폰을 찾을 수 없습니다."));

        return convertToMemberCouponDTO(memberCoupon);
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
        return convertToMemberCouponDTO(updatedMemberCoupon);
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
        return convertToMemberCouponDTO(updatedMemberCoupon);
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
                result.put("coupon", convertToDTO(coupon));
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

    private CouponDTO convertToDTO(Coupon coupon) {
        return CouponDTO.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .name(coupon.getName())
                .description(coupon.getDescription())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minOrderAmount(coupon.getMinOrderAmount())
                .maxDiscountAmount(coupon.getMaxDiscountAmount())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .usageLimit(coupon.getUsageLimit())
                .usedCount(coupon.getUsedCount())
                .status(coupon.getStatus())
                .createdAt(coupon.getCreatedAt())
                .updatedAt(coupon.getUpdatedAt())
                .build();
    }

    private MemberCouponDTO convertToMemberCouponDTO(MemberCoupon memberCoupon) {
        return MemberCouponDTO.builder()
                .id(memberCoupon.getId())
                .memberId(memberCoupon.getMember().getId())
                .coupon(convertToDTO(memberCoupon.getCoupon()))
                .used(memberCoupon.isUsed())
                .issuedAt(memberCoupon.getIssuedAt())
                .usedAt(memberCoupon.getUsedAt())
                .build();
    }
}