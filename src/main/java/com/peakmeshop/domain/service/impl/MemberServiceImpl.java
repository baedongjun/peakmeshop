package com.peakmeshop.domain.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.time.ZoneOffset;

import com.peakmeshop.api.dto.*;
import com.peakmeshop.domain.repository.PointRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.domain.entity.Member;
import com.peakmeshop.domain.entity.VerificationToken;
import com.peakmeshop.common.exception.BadRequestException;
import com.peakmeshop.domain.repository.MemberRepository;
import com.peakmeshop.domain.repository.VerificationTokenRepository;
import com.peakmeshop.domain.repository.OrderRepository;
import com.peakmeshop.domain.service.EmailService;
import com.peakmeshop.domain.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public MemberDTO getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        return convertToDTO(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDTO getMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        return convertToDTO(member);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberDTO> getAllMembers(Pageable pageable) {
        Page<Member> members = memberRepository.findAll(pageable);
        return members.map(this::convertToDTO);
    }

    @Override
    @Transactional
    public MemberDTO createMember(MemberDTO memberDTO) {
        // 이메일 중복 확인
        if (memberRepository.existsByEmail(memberDTO.getEmail())) {
            throw new BadRequestException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(memberDTO.getPassword());

        Member member = Member.builder()
                .userId(memberDTO.getUserId())
                .email(memberDTO.getEmail())
                .password(encodedPassword)
                .name(memberDTO.getName())
                .phone(memberDTO.getPhone())
                .userRole(memberDTO.getUserRole() != null ? memberDTO.getUserRole() : "ROLE_USER")
                .enabled(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .emailVerified(memberDTO.isEmailVerified())
                .agreeTerms(memberDTO.isAgreeTerms())
                .agreeMarketing(memberDTO.isAgreeMarketing())
                .isWithdrawn(memberDTO.isWithdrawn()).build();

        Member savedMember = memberRepository.save(member);

        // 인증 토큰 생성
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .member(savedMember)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();

        verificationTokenRepository.save(verificationToken);

        // 인증 이메일 발송
        emailService.sendVerificationEmail(savedMember.getEmail(), token);

        return convertToDTO(savedMember);
    }

    @Override
    @Transactional
    public MemberDTO updateMember(Long id, MemberDTO memberDTO) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        // 이메일 중복 확인 (다른 회원과 중복되는지)
        if (!member.getEmail().equals(memberDTO.getEmail()) && memberRepository.existsByEmail(memberDTO.getEmail())) {
            throw new BadRequestException("이미 사용 중인 이메일입니다.");
        }

        member.setEmail(memberDTO.getEmail());

        // 비밀번호가 제공된 경우에만 업데이트
        if (memberDTO.getPassword() != null && !memberDTO.getPassword().isEmpty()) {
            member.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
        }

        member.setName(memberDTO.getName());
        member.setPhone(memberDTO.getPhone());

        if (memberDTO.getUserRole() != null) {
            member.setUserRole(memberDTO.getUserRole());
        }

        member.setUpdatedAt(LocalDateTime.now());

        Member updatedMember = memberRepository.save(member);
        return convertToDTO(updatedMember);
    }

    @Override
    @Transactional
    public MemberDTO updateMemberProfile(Long id, MemberUpdateDTO memberUpdateDTO) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        // 이메일 중복 확인 (다른 회원과 중복되는지)
        if (memberUpdateDTO.getEmail() != null && !member.getEmail().equals(memberUpdateDTO.getEmail()) &&
                memberRepository.existsByEmail(memberUpdateDTO.getEmail())) {
            throw new BadRequestException("이미 사용 중인 이메일입니다.");
        }

        if (memberUpdateDTO.getEmail() != null) {
            member.setEmail(memberUpdateDTO.getEmail());
        }

        if (memberUpdateDTO.getName() != null) {
            member.setName(memberUpdateDTO.getName());
        }

        if (memberUpdateDTO.getPhone() != null) {
            member.setPhone(memberUpdateDTO.getPhone());
        }

        if (memberUpdateDTO.getUserRole() != null) {
            member.setUserRole(memberUpdateDTO.getUserRole());
        }

        member.setUpdatedAt(LocalDateTime.now());

        Member updatedMember = memberRepository.save(member);
        return convertToDTO(updatedMember);
    }

    @Override
    @Transactional
    public void changePassword(Long id, String currentPassword, String newPassword) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new BadRequestException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 암호화
        member.setPassword(passwordEncoder.encode(newPassword));
        member.setUpdatedAt(LocalDateTime.now());

        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        // 인증 토큰 삭제
        verificationTokenRepository.deleteByMemberId(id);

        memberRepository.delete(member);
    }

    @Override
    @Transactional
    public boolean verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("유효하지 않은 토큰입니다."));

        // 토큰 만료 확인
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(verificationToken);
            throw new BadRequestException("만료된 토큰입니다.");
        }

        // 회원 활성화
        Member member = verificationToken.getMember();
        member.setEnabled(true);
        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);

        // 토큰 삭제
        verificationTokenRepository.delete(verificationToken);

        return true;
    }

    @Override
    @Transactional
    public void resendVerificationEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        // 이미 활성화된 계정인지 확인
        if (member.isEnabled()) {
            throw new BadRequestException("이미 인증된 계정입니다.");
        }

        // 기존 토큰 삭제
        verificationTokenRepository.deleteByMemberId(member.getId());

        // 새 토큰 생성
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .member(member)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();

        verificationTokenRepository.save(verificationToken);

        // 인증 이메일 재발송
        emailService.sendVerificationEmail(member.getEmail(), token);
    }

    @Override
    @Transactional
    public void resetPassword(PasswordResetDTO passwordResetDTO) {
        Member member = memberRepository.findByEmail(passwordResetDTO.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        // 임시 비밀번호 생성
        String temporaryPassword = UUID.randomUUID().toString().substring(0, 8);

        // 비밀번호 암호화
        member.setPassword(passwordEncoder.encode(temporaryPassword));
        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);

        // 임시 비밀번호 이메일 발송
        emailService.sendPasswordResetEmail(member.getEmail(), temporaryPassword);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberDTO> searchMembers(String keyword, Pageable pageable) {
        Page<Member> members = memberRepository.findByEmailContainingOrNameContaining(keyword, keyword, pageable);
        return  members.map(this::convertToDTO);
    }

    private MemberDTO convertToDTO(Member member) {
        return MemberDTO.builder()
                .id(member.getId())
                .userId(member.getUserId())
                .email(member.getEmail())
                .name(member.getName())
                .phone(member.getPhone())
                .userRole(member.getUserRole())
                .enabled(member.isEnabled())
                .status(member.getStatus())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .agreeTerms(member.isAgreeTerms())
                .agreeMarketing(member.isAgreeMarketing())
                .isEmailVerified(member.isEmailVerified())
                .isWithdrawn(member.isWithdrawn()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDTO getMemberByUserId(String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));
        return convertToDTO(member);
    }

    @Override
    @Transactional
    public MemberDTO updateMemberStatus(Long id, String status) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));

        // 상태 유효성 검사
        if (!Member.STATUS_ACTIVE.equals(status) &&
                !Member.STATUS_INACTIVE.equals(status) &&
                !Member.STATUS_BLOCKED.equals(status)) {
            throw new BadRequestException("유효하지 않은 상태입니다.");
        }

        member.setStatus(status);
        member.setUpdatedAt(LocalDateTime.now());

        Member updatedMember = memberRepository.save(member);
        return convertToDTO(updatedMember);
    }

    @Override
    @Transactional(readOnly = true)
    public long countMembers() {
        return memberRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberGradeDTO> getAllGrades() {
        return memberRepository.findAllGrades().stream()
                .map(grade -> MemberGradeDTO.builder()
                        .id(grade.getId())
                        .name(grade.getName())
                        .conditionType(grade.getConditionType())
                        .conditionValue(grade.getConditionValue())
                        .benefitType(grade.getBenefitType())
                        .benefitValue(grade.getBenefitValue())
                        .isFreeShipping(grade.isFreeShipping())
                        .isActive(grade.isActive())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MemberGradeDTO getGradeById(Long id) {
        return memberRepository.findGradeById(id)
                .map(grade -> MemberGradeDTO.builder()
                        .id(grade.getId())
                        .name(grade.getName())
                        .conditionType(grade.getConditionType())
                        .conditionValue(grade.getConditionValue())
                        .benefitType(grade.getBenefitType())
                        .benefitValue(grade.getBenefitValue())
                        .isFreeShipping(grade.isFreeShipping())
                        .isActive(grade.isActive())
                        .build())
                .orElseThrow(() -> new EntityNotFoundException("등급을 찾을 수 없습니다."));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getMemberSummary() {
        return Map.of(
            "total", memberRepository.count(),
            "active", memberRepository.countByStatus(Member.STATUS_ACTIVE),
            "inactive", memberRepository.countByStatus(Member.STATUS_INACTIVE),
            "blocked", memberRepository.countByStatus(Member.STATUS_BLOCKED),
            "dormant", memberRepository.countByStatus(Member.STATUS_DORMANT)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getMemberSummary(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        return Map.of(
            "totalPoints", pointRepository.sumPointsByMemberId(memberId),
            "totalOrders", orderRepository.countByMemberId(memberId),
            "totalSpent", orderRepository.sumTotalAmountByMemberId(memberId),
            "lastOrderDate", member.getLastOrderDate() != null ? 
                member.getLastOrderDate().toEpochSecond(ZoneOffset.UTC) : 0L
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getMemberStatistics(String period, String startDate, String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME);

        Map<String, Object> statistics = new HashMap<>();
        
        // 기간별 회원 등록 통계
        statistics.put("registrations", memberRepository.countByCreatedAtBetween(start, end));
        
        // 기간별 회원 상태 변경 통계
        statistics.put("statusChanges", memberRepository.countStatusChangesBetween(start, end));
        
        // 기간별 포인트 적립/사용 통계
        statistics.put("points", pointRepository.sumPointsByDateRange(start, end));
        
        // 기간별 주문 통계
        statistics.put("orders", orderRepository.countByCreatedAtBetween(start, end));
        
        return statistics;
    }

    @Override
    public MemberSummaryDTO getMemberSummary(String period, String startDate, String endDate) {
        LocalDateTime start;
        LocalDateTime end;

        // 기간 설정
        if (startDate != null && endDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            start = LocalDate.parse(startDate, formatter).atStartOfDay();
            end = LocalDate.parse(endDate, formatter).plusDays(1).atStartOfDay();
        } else {
            LocalDate now = LocalDate.now();
            if (period != null) {
                switch (period) {
                    case "daily":
                        start = now.atStartOfDay();
                        end = now.plusDays(1).atStartOfDay();
                        break;
                    case "weekly":
                        start = now.minusWeeks(1).atStartOfDay();
                        end = now.plusDays(1).atStartOfDay();
                        break;
                    case "monthly":
                        start = now.minusMonths(1).atStartOfDay();
                        end = now.plusDays(1).atStartOfDay();
                        break;
                    case "yearly":
                        start = now.minusYears(1).atStartOfDay();
                        end = now.plusDays(1).atStartOfDay();
                        break;
                    default:
                        start = now.minusMonths(1).atStartOfDay();
                        end = now.plusDays(1).atStartOfDay();
                }
            } else {
                // period가 null인 경우에 대한 기본 케이스 처리
                start = now.minusMonths(1).atStartOfDay();
                end = now.plusDays(1).atStartOfDay();
            }
        }

        // 회원 데이터 조회
        List<Member> members = memberRepository.findByCreatedAtBetween(start, end);
        long totalMembers = memberRepository.count();
        long activeMembers = memberRepository.countByStatus("ACTIVE");
        long inactiveMembers = memberRepository.countByStatus("INACTIVE");

        // 회원별 주문/매출 통계
        double totalRevenue = orderRepository.calculateTotalRevenue(start, end);
        long totalOrders = orderRepository.countByCreatedAtBetween(start, end);
        double averageOrdersPerMember = totalMembers == 0 ? 0 : (double) totalOrders / totalMembers;
        double averageRevenuePerMember = totalMembers == 0 ? 0 : totalRevenue / totalMembers;

        // 포인트 통계
        double totalPoints = pointRepository.calculateTotalPoints(LocalDateTime.now());
        double averagePoints = totalMembers == 0 ? 0 : totalPoints / totalMembers;

        return MemberSummaryDTO.builder()
                .totalMembers(totalMembers)
                .monthlyNewMembers(memberRepository.countByCreatedAtBetween(
                        LocalDateTime.now().minusMonths(1),
                        LocalDateTime.now()))
                .dailyNewMembers(memberRepository.countByCreatedAtBetween(
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now()))
                .activeMembers(activeMembers)
                .inactiveMembers(inactiveMembers)
                .averageOrdersPerMember(averageOrdersPerMember)
                .averageRevenuePerMember(averageRevenuePerMember)
                .totalPoints(totalPoints)
                .averagePoints(averagePoints)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PointDTO> getPoints(Pageable pageable) {
        return pointRepository.findAll(pageable)
                .map(point -> PointDTO.builder()
                        .id(point.getId())
                        .member(point.getMember())
                        .memberName(point.getMember().getName())
                        .type(point.getType())
                        .amount(point.getAmount())
                        .createdAt(point.getCreatedAt())
                        .build());
    }

    @Override
    @Transactional(readOnly = true)
    public PointDTO getPointById(Long id) {
        return pointRepository.findById(id)
                .map(point -> PointDTO.builder()
                        .id(point.getId())
                        .member(point.getMember())
                        .memberName(point.getMember().getName())
                        .type(point.getType())
                        .amount(point.getAmount())
                        .createdAt(point.getCreatedAt())
                        .build())
                .orElseThrow(() -> new EntityNotFoundException("포인트 내역을 찾을 수 없습니다."));
    }

    @Override
    @Transactional
    public void requestPasswordReset(PasswordResetRequestDTO requestDTO) {
        Member member = memberRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));

        // 비밀번호 재설정 토큰 생성
        String token = UUID.randomUUID().toString();

        // 토큰 저장 (예: 별도의 테이블이나 Redis 등에 저장)
        // 여기서는 간단히 이메일 발송만 처리

        // 비밀번호 재설정 이메일 발송
        emailService.sendPasswordResetRequestEmail(member.getEmail(), token);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberDTO> getWithdrawnMembers(String startDate, String endDate, Pageable pageable) {
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : null;
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : null;
        
        Page<Member> members;
        if (start != null && end != null) {
            members = memberRepository.findByIsWithdrawnTrueAndWithdrawnAtBetween(start, end, pageable);
        } else {
            members = memberRepository.findByIsWithdrawnTrue(pageable);
        }
        
        return members.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberDTO> getDormantMembers(Pageable pageable) {
        LocalDateTime threshold = LocalDateTime.now().minusMonths(12);
        return memberRepository.findDormantMembers(threshold, pageable)
                .map(this::convertToDTO);
    }
}