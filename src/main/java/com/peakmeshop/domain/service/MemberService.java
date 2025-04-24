package com.peakmeshop.domain.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.MemberDTO;
import com.peakmeshop.api.dto.MemberUpdateDTO;
import com.peakmeshop.api.dto.PasswordResetDTO;
import com.peakmeshop.api.dto.PasswordResetRequestDTO;
import com.peakmeshop.api.dto.MemberGradeDTO;
import com.peakmeshop.api.dto.PointDTO;
import com.peakmeshop.api.dto.MemberSummaryDTO;

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

    List<MemberGradeDTO> getAllGrades();

    MemberGradeDTO getGradeById(Long id);

    MemberSummaryDTO getTotalMemberSummary();

    MemberSummaryDTO getMemberSummary(Long memberId);

    Map<String, Object> getMemberStatistics(String period, String startDate, String endDate);

    Page<PointDTO> getPoints(Pageable pageable);

    PointDTO getPointById(Long id);

    Page<MemberDTO> getDormantMembers(Pageable pageable);

    Page<MemberDTO> getWithdrawnMembers(String startDate, String endDate, Pageable pageable);

    /**
     * 회원 통계 정보 조회
     * @param period 기간 (daily, weekly, monthly, yearly)
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 회원 통계 정보
     */
    MemberSummaryDTO getMemberSummary(String period, String startDate, String endDate);
}