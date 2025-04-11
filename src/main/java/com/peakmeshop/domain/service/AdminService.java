package com.peakmeshop.domain.service;

import com.peakmeshop.api.dto.AdminNotificationSettingsDTO;
import com.peakmeshop.api.dto.AdminProfileDTO;
import com.peakmeshop.domain.entity.Admin;
import com.peakmeshop.domain.entity.AdminSession;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminService {
    /**
     * 현재 로그인한 관리자 정보를 가져옵니다.
     * @return 관리자 정보
     */
    Admin getCurrentAdmin();

    /**
     * 관리자 프로필을 업데이트합니다.
     * @param profileDTO 프로필 정보
     * @return 업데이트된 관리자 정보
     */
    Admin updateProfile(AdminProfileDTO profileDTO);

    /**
     * 관리자 비밀번호를 변경합니다.
     * @param currentPassword 현재 비밀번호
     * @param newPassword 새 비밀번호
     * @param confirmPassword 비밀번호 확인
     * @return 업데이트된 관리자 정보
     */
    Admin changePassword(String currentPassword, String newPassword, String confirmPassword);

    /**
     * 관리자 프로필 이미지를 업데이트합니다.
     * @param profileImage 프로필 이미지
     * @return 업데이트된 관리자 정보
     */
    Admin updateProfileImage(MultipartFile profileImage);

    /**
     * 2단계 인증을 활성화합니다.
     * @param authCode 인증 코드
     * @return 업데이트된 관리자 정보
     */
    Admin enableTwoFactorAuth(String authCode);

    /**
     * 2단계 인증을 비활성화합니다.
     * @return 업데이트된 관리자 정보
     */
    Admin disableTwoFactorAuth();

    /**
     * 백업 코드를 가져옵니다.
     * @param adminId 관리자 ID
     * @return 백업 코드 목록
     */
    List<String> getBackupCodes(Long adminId);

    /**
     * 알림 설정을 가져옵니다.
     * @param adminId 관리자 ID
     * @return 알림 설정
     */
    AdminNotificationSettingsDTO getNotificationSettings(Long adminId);

    /**
     * 알림 설정을 업데이트합니다.
     * @param settingsDTO 알림 설정
     * @return 업데이트된 알림 설정
     */
    AdminNotificationSettingsDTO updateNotificationSettings(AdminNotificationSettingsDTO settingsDTO);

    /**
     * 관리자 세션 목록을 가져옵니다.
     * @param adminId 관리자 ID
     * @return 세션 목록
     */
    List<AdminSession> getAdminSessions(Long adminId);

    /**
     * 세션을 종료합니다.
     * @param sessionId 세션 ID
     */
    void terminateSession(Long sessionId);

    /**
     * 현재 세션을 제외한 모든 세션을 종료합니다.
     * @param currentSessionId 현재 세션 ID
     */
    void terminateAllSessionsExcept(Long currentSessionId);
}