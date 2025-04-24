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
import com.peakmeshop.api.mapper.MemberMapper;
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
    private final MemberMapper memberMapper;

    @Override
    @Transactional(readOnly = true)
    public MemberDTO getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        return memberMapper.toDTO(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDTO getMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        return memberMapper.toDTO(member);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberDTO> getAllMembers(Pageable pageable) {
        Page<Member> members = memberRepository.findAll(pageable);
        return members.map(memberMapper::toDTO);
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
        memberDTO.setPassword(encodedPassword);

        Member member = memberMapper.toEntity(memberDTO);
        member.setEnabled(false);
        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());

        Member savedMember = memberRepository.save(member);

        // 인증 토큰 생성
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .member(savedMember)
                .expiryAt(LocalDateTime.now().plusHours(24))
                .build();

        verificationTokenRepository.save(verificationToken);

        // 인증 이메일 발송
        emailService.sendVerificationEmail(savedMember.getEmail(), token);

        return memberMapper.toDTO(savedMember);
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

        // 비밀번호가 제공된 경우에만 암호화하여 업데이트
        if (memberDTO.getPassword() != null && !memberDTO.getPassword().isEmpty()) {
            memberDTO.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
        }

        Member updatedMember = memberMapper.toEntity(memberDTO);
        updatedMember.setId(member.getId());
        updatedMember.setUpdatedAt(LocalDateTime.now());

        Member savedMember = memberRepository.save(updatedMember);
        return memberMapper.toDTO(savedMember);
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
        return memberMapper.toDTO(updatedMember);
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
        if (verificationToken.getExpiryAt().isBefore(LocalDateTime.now())) {
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
                .expiryAt(LocalDateTime.now().plusHours(24))
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
        return  members.map(memberMapper::toDTO);
    }

    private MemberDTO convertToDTO(Member member) {
        return memberMapper.toDTO(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDTO getMemberByUserId(String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));
        return memberMapper.toDTO(member);
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
        return memberMapper.toDTO(updatedMember);
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
    @Transactional(readOnly = true)
    public MemberSummaryDTO getTotalMemberSummary() {
        return MemberSummaryDTO.createTotalSummary(
            memberRepository.count(),
            memberRepository.countByCreatedAtBetween(LocalDateTime.now().minusMonths(1), LocalDateTime.now()),
            memberRepository.countByCreatedAtBetween(LocalDateTime.now().minusDays(1), LocalDateTime.now()),
            memberRepository.countByStatus(Member.STATUS_ACTIVE),
            memberRepository.countByStatus(Member.STATUS_INACTIVE),
            memberRepository.countByStatus(Member.STATUS_BLOCKED),
            memberRepository.countByStatus(Member.STATUS_DORMANT)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public MemberSummaryDTO getMemberSummary(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        return MemberSummaryDTO.createMemberSummary(
            pointRepository.sumPointsByMemberId(memberId),
            orderRepository.countByMemberId(memberId),
            orderRepository.sumTotalAmountByMemberId(memberId),
            member.getLastOrderDate()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public MemberSummaryDTO getMemberSummary(String period, String startDate, String endDate) {
        LocalDateTime start = null;
        LocalDateTime end = LocalDateTime.now();
        
        if (period != null) {
            switch (period) {
                case "daily":
                    start = end.minusDays(30);
                    break;
                case "weekly":
                    start = end.minusWeeks(12);
                    break;
                case "monthly":
                    start = end.minusMonths(12);
                    break;
                case "yearly":
                    start = end.minusYears(5);
                    break;
                default:
                    start = LocalDate.parse(startDate).atStartOfDay();
                    end = LocalDate.parse(endDate).atTime(23, 59, 59);
            }
        } else {
            start = LocalDate.parse(startDate).atStartOfDay();
            end = LocalDate.parse(endDate).atTime(23, 59, 59);
        }

        long totalMembers = memberRepository.count();
        long monthlyNewMembers = memberRepository.countByCreatedAtBetween(LocalDateTime.now().minusMonths(1), LocalDateTime.now());
        long dailyNewMembers = memberRepository.countByCreatedAtBetween(LocalDateTime.now().minusDays(1), LocalDateTime.now());
        long statusChanges = memberRepository.countStatusChangesBetween(start, end);
        long activeMembers = memberRepository.countByStatus(Member.STATUS_ACTIVE);
        long inactiveMembers = memberRepository.countByStatus(Member.STATUS_INACTIVE);
        long blockedMembers = memberRepository.countByStatus(Member.STATUS_BLOCKED);

        return MemberSummaryDTO.createTotalSummary(
            totalMembers,
            monthlyNewMembers,
            dailyNewMembers,
            activeMembers,
            inactiveMembers,
            blockedMembers,
            memberRepository.countByStatus(Member.STATUS_DORMANT)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PointDTO> getPoints(Pageable pageable) {
        return pointRepository.findAll(pageable)
                .map(point -> PointDTO.builder()
                        .id(point.getId())
                        .memberId(point.getMember().getId())
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
                        .memberId(point.getMember().getId())
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
        
        return members.map(memberMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberDTO> getDormantMembers(Pageable pageable) {
        LocalDateTime threshold = LocalDateTime.now().minusMonths(12);
        return memberRepository.findDormantMembers(threshold, pageable)
                .map(memberMapper::toDTO);
    }
}