package com.peakmeshop.service.Impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.dto.MemberDTO;
import com.peakmeshop.entity.Member;
import com.peakmeshop.exception.ResourceNotFoundException;
import com.peakmeshop.repository.MemberRepository;
import com.peakmeshop.service.MemberService;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberServiceImpl(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public MemberDTO register(MemberDTO memberDTO) {
        if (memberRepository.existsByEmail(memberDTO.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + memberDTO.email());
        }

        var member = convertToEntity(memberDTO);
        member.setPassword(passwordEncoder.encode(memberDTO.password()));
        member.setCreatedAt(LocalDateTime.now());
        member.setIsActive(true);
        member.setRole(memberDTO.role() != null ? memberDTO.role() : "ROLE_USER");

        var savedMember = memberRepository.save(member);
        return convertToDTO(savedMember).withoutPassword();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MemberDTO> findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .map(this::convertToDTO)
                .map(MemberDTO::withoutPassword);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MemberDTO> findById(Long id) {
        return memberRepository.findById(id)
                .map(this::convertToDTO)
                .map(MemberDTO::withoutPassword);
    }

    @Override
    @Transactional
    public MemberDTO updateMember(Long id, MemberDTO memberDTO) {
        var member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다: " + id));

        // 이메일 변경 시 중복 확인
        if (!member.getEmail().equals(memberDTO.email()) &&
                memberRepository.existsByEmail(memberDTO.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + memberDTO.email());
        }

        // 기존 정보 유지
        member.setEmail(memberDTO.email());
        member.setName(memberDTO.name());
        member.setPhone(memberDTO.phone());
        member.setAddress(memberDTO.address());
        member.setDetailAddress(memberDTO.detailAddress());
        member.setZipCode(memberDTO.zipCode());
        member.setUpdatedAt(LocalDateTime.now());

        // 관리자가 역할 변경 가능
        if (memberDTO.role() != null) {
            member.setRole(memberDTO.role());
        }

        var updatedMember = memberRepository.save(member);
        return convertToDTO(updatedMember).withoutPassword();
    }

    @Override
    @Transactional
    public boolean changePassword(Long id, String currentPassword, String newPassword) {
        var member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다: " + id));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            return false;
        }

        member.setPassword(passwordEncoder.encode(newPassword));
        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);

        return true;
    }

    @Override
    @Transactional
    public void deactivateMember(Long id) {
        var member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다: " + id));

        member.setIsActive(false);
        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void activateMember(Long id) {
        var member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다: " + id));

        member.setIsActive(true);
        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new ResourceNotFoundException("회원을 찾을 수 없습니다: " + id);
        }
        memberRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberDTO> searchMembers(String keyword, Pageable pageable) {
        return memberRepository.searchMembers(keyword, pageable)
                .map(this::convertToDTO)
                .map(MemberDTO::withoutPassword);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailDuplicated(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public void updateLastLoginTime(String email) {
        memberRepository.findByEmail(email).ifPresent(member -> {
            member.setLastLoginAt(LocalDateTime.now());
            memberRepository.save(member);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        if (!member.getIsActive()) {
            throw new UsernameNotFoundException("비활성화된 계정입니다: " + email);
        }

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getRole()));

        return new User(member.getEmail(), member.getPassword(), authorities);
    }

    // Entity -> DTO 변환
    private MemberDTO convertToDTO(Member member) {
        return MemberDTO.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(member.getPassword()) // 서비스 레이어에서 필요 시 제거
                .name(member.getName())
                .phone(member.getPhone())
                .address(member.getAddress())
                .detailAddress(member.getDetailAddress())
                .zipCode(member.getZipCode())
                .role(member.getRole())
                .isActive(member.getIsActive())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .lastLoginAt(member.getLastLoginAt())
                .build();
    }

    // DTO -> Entity 변환
    private Member convertToEntity(MemberDTO dto) {
        var member = new Member();
        member.setId(dto.id());
        member.setEmail(dto.email());
        // 비밀번호는 별도 처리
        member.setName(dto.name());
        member.setPhone(dto.phone());
        member.setAddress(dto.address());
        member.setDetailAddress(dto.detailAddress());
        member.setZipCode(dto.zipCode());
        member.setRole(dto.role());
        member.setIsActive(dto.isActive());
        member.setCreatedAt(dto.createdAt());
        member.setUpdatedAt(dto.updatedAt());
        member.setLastLoginAt(dto.lastLoginAt());

        return member;
    }
}