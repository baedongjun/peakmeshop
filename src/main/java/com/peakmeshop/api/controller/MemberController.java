package com.peakmeshop.api.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peakmeshop.api.dto.ApiResponse;
import com.peakmeshop.api.dto.MemberDTO;
import com.peakmeshop.api.dto.MemberUpdateDTO;
import com.peakmeshop.api.dto.PasswordResetDTO;
import com.peakmeshop.domain.entity.Member;
import com.peakmeshop.domain.repository.MemberRepository;
import com.peakmeshop.common.security.oauth2.user.UserPrincipal;
import com.peakmeshop.domain.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<MemberDTO>> getAllMembers(@PageableDefault(size = 10) Pageable pageable) {
        Page<MemberDTO> members = memberService.getAllMembers(pageable);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MemberDTO>> searchMembers(@RequestParam String keyword, @PageableDefault(size = 10) Pageable pageable) {
        List<MemberDTO> members = memberService.searchMembers(keyword, pageable);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable Long id) {
        MemberDTO member = memberService.getMemberById(id);
        return ResponseEntity.ok(member);
    }

    @GetMapping("/me")
    public ResponseEntity<MemberDTO> getCurrentMember(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        MemberDTO member = memberService.getMemberById(userPrincipal.getId());
        return ResponseEntity.ok(member);
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberDTO> getMemberByEmail(@PathVariable String email) {
        MemberDTO member = memberService.getMemberByEmail(email);
        return ResponseEntity.ok(member);
    }

    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse> checkEmailExists(@RequestParam String email) {
        boolean exists = memberService.existsByEmail(email);
        return ResponseEntity.ok(new ApiResponse(exists, exists ? "이미 사용 중인 이메일입니다." : "사용 가능한 이메일입니다."));
    }

    @GetMapping("/profile")
    public ResponseEntity<MemberDTO> getMemberProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Member member = memberRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        MemberDTO memberDTO = MemberDTO.builder()
                .id(member.getId())
                .userId(member.getUserId())
                .email(member.getEmail())
                .name(member.getName())
                .phone(member.getPhone())
                .userRole(member.getUserRole())
                .status(member.getStatus())
                .enabled(member.isActive())
                .emailVerified(member.isEmailVerified())
                .providerType(member.getProviderType())
                .providerId(member.getProviderId())
                .imageUrl(member.getImageUrl())
                .birthDate(member.getBirthDate())
                .gender(member.getGender())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();

        return ResponseEntity.ok(memberDTO);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberDTO> createMember(@RequestBody MemberDTO memberDTO) {
        MemberDTO createdMember = memberService.createMember(memberDTO);
        return new ResponseEntity<>(createdMember, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<MemberDTO> updateMember(@PathVariable Long id, @RequestBody MemberDTO memberDTO) {
        MemberDTO updatedMember = memberService.updateMember(id, memberDTO);
        return ResponseEntity.ok(updatedMember);
    }

    @PutMapping("/{id}/profile")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<MemberDTO> updateMemberProfile(@PathVariable Long id, @RequestBody MemberUpdateDTO memberUpdateDTO) {
        MemberDTO updatedMember = memberService.updateMemberProfile(id, memberUpdateDTO);
        return ResponseEntity.ok(updatedMember);
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<ApiResponse> changePassword(
            @PathVariable Long id,
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        memberService.changePassword(id, currentPassword, newPassword);
        return ResponseEntity.ok(new ApiResponse(true, "비밀번호가 변경되었습니다."));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<ApiResponse> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.ok(new ApiResponse(true, "회원이 삭제되었습니다."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam String email) {
        PasswordResetDTO passwordResetDTO = new PasswordResetDTO();
        passwordResetDTO.setEmail(email);
        memberService.resetPassword(passwordResetDTO);
        return ResponseEntity.ok(new ApiResponse(true, "임시 비밀번호가 이메일로 발송되었습니다."));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestParam String token) {
        boolean verified = memberService.verifyEmail(token);
        return ResponseEntity.ok(new ApiResponse(verified, verified ? "이메일이 인증되었습니다." : "이메일 인증에 실패했습니다."));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse> resendVerificationEmail(@RequestParam String email) {
        memberService.resendVerificationEmail(email);
        return ResponseEntity.ok(new ApiResponse(true, "인증 이메일이 재발송되었습니다."));
    }
}