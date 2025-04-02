package com.peakmeshop.domain.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.MemberDTO;
import com.peakmeshop.api.dto.MemberUpdateDTO;
import com.peakmeshop.api.dto.PasswordResetDTO;
import com.peakmeshop.api.dto.PasswordResetRequestDTO;

public interface MemberService {

    MemberDTO getMemberById(Long id);

    MemberDTO getMemberByEmail(String email);

    MemberDTO getMemberByUserId(String userId);

    Page<MemberDTO> getAllMembers(Pageable pageable);

    MemberDTO createMember(MemberDTO memberDTO);

    MemberDTO updateMember(Long id, MemberDTO memberDTO);

    MemberDTO updateMemberProfile(Long id, MemberUpdateDTO memberUpdateDTO);

    MemberDTO updateMemberStatus(Long id, String status);

    void changePassword(Long id, String currentPassword, String newPassword);

    void deleteMember(Long id);

    boolean verifyEmail(String token);

    void resendVerificationEmail(String email);

    void resetPassword(PasswordResetDTO passwordResetDTO);

    void requestPasswordReset(PasswordResetRequestDTO requestDTO);

    boolean existsByEmail(String email);

    Page<MemberDTO> searchMembers(String keyword, Pageable pageable);

    long countMembers();
}