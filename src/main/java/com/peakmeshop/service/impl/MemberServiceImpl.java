package com.peakmeshop.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.peakmeshop.dto.PasswordResetDTO;
import com.peakmeshop.dto.PasswordResetRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.dto.MemberDTO;
import com.peakmeshop.dto.MemberUpdateDTO;
import com.peakmeshop.entity.Member;
import com.peakmeshop.entity.VerificationToken;
import com.peakmeshop.exception.BadRequestException;
import com.peakmeshop.exception.ResourceNotFoundException;
import com.peakmeshop.repository.MemberRepository;
import com.peakmeshop.repository.VerificationTokenRepository;
import com.peakmeshop.service.EmailService;
import com.peakmeshop.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional(readOnly = true)
    public MemberDTO getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));
        return convertToDTO(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDTO getMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));
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
                .email(memberDTO.getEmail())
                .password(encodedPassword)
                .name(memberDTO.getName())
                .phone(memberDTO.getPhone())
                .userRole(memberDTO.getUserRole() != null ? memberDTO.getUserRole() : "ROLE_USER")
                .enabled(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

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
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

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
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

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
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

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
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

        // 인증 토큰 삭제
        verificationTokenRepository.deleteByMemberId(id);

        memberRepository.delete(member);
    }

    @Override
    @Transactional
    public boolean verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("유효하지 않은 토큰입니다."));

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
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

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
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

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
    public List<MemberDTO> searchMembers(String keyword, Pageable pageable) {
        Page<Member> members = memberRepository.findByEmailContainingOrNameContaining(keyword, keyword, pageable);
        return members.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private MemberDTO convertToDTO(Member member) {
        return MemberDTO.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .phone(member.getPhone())
                .userRole(member.getUserRole())
                .enabled(member.isEnabled())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDTO getMemberByUserId(String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));
        return convertToDTO(member);
    }

    @Override
    @Transactional
    public MemberDTO updateMemberStatus(Long id, String status) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

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
    @Transactional
    public void requestPasswordReset(PasswordResetRequestDTO requestDTO) {
        Member member = memberRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

        // 비밀번호 재설정 토큰 생성
        String token = UUID.randomUUID().toString();

        // 토큰 저장 (예: 별도의 테이블이나 Redis 등에 저장)
        // 여기서는 간단히 이메일 발송만 처리

        // 비밀번호 재설정 이메일 발송
        emailService.sendPasswordResetRequestEmail(member.getEmail(), token);
    }
}