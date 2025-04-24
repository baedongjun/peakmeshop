package com.peakmeshop.domain.service.impl;

import com.peakmeshop.api.dto.AdminNotificationSettingsDTO;
import com.peakmeshop.api.dto.AdminProfileDTO;
import com.peakmeshop.common.annotation.LogActivity;
import com.peakmeshop.domain.entity.ActivityLog;
import com.peakmeshop.domain.entity.Admin;
import com.peakmeshop.domain.entity.AdminSession;
import com.peakmeshop.domain.entity.Member;
import com.peakmeshop.domain.repository.ActivityLogRepository;
import com.peakmeshop.domain.repository.AdminRepository;
import com.peakmeshop.domain.repository.AdminSessionRepository;
import com.peakmeshop.domain.repository.MemberRepository;
import com.peakmeshop.domain.service.AdminService;
import com.peakmeshop.domain.service.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final AdminSessionRepository adminSessionRepository;
    private final MemberRepository memberRepository;
    private final ActivityLogRepository activityLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final HttpSession httpSession;

    @Override
    @Transactional(readOnly = true)
    public Admin getCurrentAdmin() {
        // Spring Security에서 현재 인증된 사용자 정보를 가져옵니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증된 사용자가 없습니다.");
        }

        String userId = authentication.getName();
        return adminRepository.findByMemberUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("관리자를 찾을 수 없습니다: " + userId));
    }

    @Override
    @Transactional
    @LogActivity(
            type = "PROFILE_UPDATE",
            description = "관리자 프로필 정보가 업데이트되었습니다.",
            referenceType = "ADMIN",
            logParams = true
    )
    public Admin updateProfile(AdminProfileDTO profileDTO) {
        Admin admin = adminRepository.findById(profileDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("관리자를 찾을 수 없습니다: " + profileDTO.getId()));

        // 멤버 정보 업데이트
        Member member = admin.getMember();
        if (member == null) {
            throw new IllegalStateException("관리자에 연결된 멤버 정보가 없습니다.");
        }

        member.setName(profileDTO.getName());
        member.setEmail(profileDTO.getEmail());
        member.setPhone(profileDTO.getPhone());
        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);

        // 관리자 정보 업데이트
        admin.setPosition(profileDTO.getPosition());
        admin.setTimezone(profileDTO.getTimezone());
        admin.setLanguage(profileDTO.getLanguage());
        admin.setUpdatedAt(LocalDateTime.now());

        // 활동 로그 기록
        ActivityLog activityLog = ActivityLog.builder()
                .type("PROFILE_UPDATE")
                .description("프로필 정보가 업데이트되었습니다.")
                .referenceType("ADMIN")
                .referenceId(admin.getId())
                .userAgent(httpSession.getAttribute("userAgent") != null ? httpSession.getAttribute("userAgent").toString() : "Unknown")
                .ipAddress(httpSession.getAttribute("ipAddress") != null ? httpSession.getAttribute("ipAddress").toString() : "Unknown")
                .userId(admin.getMember().getUserId())
                .createdAt(LocalDateTime.now())
                .build();
        activityLogRepository.save(activityLog);

        return adminRepository.save(admin);
    }

    @Override
    @Transactional
    @LogActivity(
            type = "PASSWORD_CHANGE",
            description = "관리자 비밀번호가 변경되었습니다.",
            referenceType = "ADMIN"
    )
    public Admin changePassword(String currentPassword, String newPassword, String confirmPassword) {
        // 현재 로그인한 관리자 정보 가져오기
        Admin admin = getCurrentAdmin();
        Member member = admin.getMember();

        if (member == null) {
            throw new IllegalStateException("관리자에 연결된 멤버 정보가 없습니다.");
        }

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호와 확인 비밀번호 일치 확인
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        // 비밀번호 복잡성 검증
        validatePasswordComplexity(newPassword);

        // 비밀번호 업데이트
        member.setPassword(passwordEncoder.encode(newPassword));
        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);

        // 관리자 정보 업데이트
        admin.setUpdatedAt(LocalDateTime.now());

        // 활동 로그 기록
        ActivityLog activityLog = ActivityLog.builder()
                .type("PASSWORD_CHANGE")
                .description("비밀번호가 변경되었습니다.")
                .referenceType("ADMIN")
                .referenceId(admin.getId())
                .userAgent(httpSession.getAttribute("userAgent") != null ? httpSession.getAttribute("userAgent").toString() : "Unknown")
                .ipAddress(httpSession.getAttribute("ipAddress") != null ? httpSession.getAttribute("ipAddress").toString() : "Unknown")
                .createdAt(LocalDateTime.now())
                .build();
        activityLogRepository.save(activityLog);

        return adminRepository.save(admin);
    }

    @Override
    @Transactional
    @LogActivity(
            type = "PROFILE_IMAGE_UPDATE",
            description = "관리자 프로필 이미지가 업데이트되었습니다.",
            referenceType = "ADMIN"
    )
    public Admin updateProfileImage(MultipartFile profileImage) {
        // 현재 로그인한 관리자 정보 가져오기
        Admin admin = getCurrentAdmin();
        Member member = admin.getMember();

        if (member == null) {
            throw new IllegalStateException("관리자에 연결된 멤버 정보가 없습니다.");
        }

        try {
            // 이미지 저장
            String imageUrl = fileStorageService.storeFile(profileImage, "admin/profile");

            // 이전 이미지가 있으면 삭제
            if (admin.getProfileImageUrl() != null && !admin.getProfileImageUrl().isEmpty()) {
                fileStorageService.deleteFile(admin.getProfileImageUrl());
            }

            // 프로필 이미지 URL 업데이트
            admin.setProfileImageUrl(imageUrl);
            admin.setUpdatedAt(LocalDateTime.now());

            // 멤버 프로필 이미지도 업데이트
            member.setUpdatedAt(LocalDateTime.now());
            memberRepository.save(member);

            // 활동 로그 기록
            ActivityLog activityLog = ActivityLog.builder()
                    .type("PROFILE_IMAGE_UPDATE")
                    .description("프로필 이미지가 업데이트되었습니다.")
                    .referenceType("ADMIN")
                    .referenceId(admin.getId())
                    .userAgent(httpSession.getAttribute("userAgent") != null ? httpSession.getAttribute("userAgent").toString() : "Unknown")
                    .ipAddress(httpSession.getAttribute("ipAddress") != null ? httpSession.getAttribute("ipAddress").toString() : "Unknown")
                    .createdAt(LocalDateTime.now())
                    .userId(admin.getMember().getUserId())
                    .build();
            activityLogRepository.save(activityLog);

            return adminRepository.save(admin);
        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 업로드 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    @Transactional
    public Admin enableTwoFactorAuth(String authCode) {
        // 현재 로그인한 관리자 정보 가져오기
        Admin admin = getCurrentAdmin();
        Member member = admin.getMember();

        if (member == null) {
            throw new IllegalStateException("관리자에 연결된 멤버 정보가 없습니다.");
        }

        // 2단계 인증이 이미 활성화되어 있는지 확인
        if (admin.isTwoFactorEnabled()) {
            throw new IllegalStateException("2단계 인증이 이미 활성화되어 있습니다.");
        }

        // 2단계 인증 비밀키 생성
        String secret = generateTwoFactorSecret();

        // QR 코드 URL 생성 (userId 사용)
        String qrCodeUrl = generateTwoFactorQrCodeUrl(member.getUserId(), secret);

        // 인증 코드 검증
        if (!validateTwoFactorAuthCode(secret, authCode)) {
            throw new IllegalArgumentException("인증 코드가 올바르지 않습니다.");
        }

        // 2단계 인증 활성화
        admin.setTwoFactorEnabled(true);
        admin.setTwoFactorSecret(secret);
        admin.setTwoFactorQrCodeUrl(qrCodeUrl);
        admin.setUpdatedAt(LocalDateTime.now());

        // 활동 로그 기록
        ActivityLog activityLog = ActivityLog.builder()
                .type("TWO_FACTOR_ENABLE")
                .description("2단계 인증이 활성화되었습니다.")
                .referenceType("ADMIN")
                .referenceId(admin.getId())
                .userAgent(httpSession.getAttribute("userAgent") != null ? httpSession.getAttribute("userAgent").toString() : "Unknown")
                .ipAddress(httpSession.getAttribute("ipAddress") != null ? httpSession.getAttribute("ipAddress").toString() : "Unknown")
                .createdAt(LocalDateTime.now())
                .userId(admin.getMember().getUserId())
                .build();
        activityLogRepository.save(activityLog);

        return adminRepository.save(admin);
    }

    @Override
    @Transactional
    public Admin disableTwoFactorAuth() {
        // 현재 로그인한 관리자 정보 가져오기
        Admin admin = getCurrentAdmin();

        // 2단계 인증이 활성화되어 있는지 확인
        if (!admin.isTwoFactorEnabled()) {
            throw new IllegalStateException("2단계 인증이 활성화되어 있지 않습니다.");
        }

        // 2단계 인증 비활성화
        admin.setTwoFactorEnabled(false);
        admin.setTwoFactorSecret(null);
        admin.setTwoFactorQrCodeUrl(null);
        admin.setUpdatedAt(LocalDateTime.now());

        // 활동 로그 기록
        ActivityLog activityLog = ActivityLog.builder()
                .type("TWO_FACTOR_DISABLE")
                .description("2단계 인증이 비활성화되었습니다.")
                .referenceType("ADMIN")
                .referenceId(admin.getId())
                .userAgent(httpSession.getAttribute("userAgent") != null ? httpSession.getAttribute("userAgent").toString() : "Unknown")
                .ipAddress(httpSession.getAttribute("ipAddress") != null ? httpSession.getAttribute("ipAddress").toString() : "Unknown")
                .createdAt(LocalDateTime.now())
                .userId(admin.getMember().getUserId())
                .build();
        activityLogRepository.save(activityLog);

        return adminRepository.save(admin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getBackupCodes(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자를 찾을 수 없습니다: " + adminId));

        // 2단계 인증이 활성화되어 있는지 확인
        if (!admin.isTwoFactorEnabled()) {
            throw new IllegalStateException("2단계 인증이 활성화되어 있지 않습니다.");
        }

        // 백업 코드 생성 (실제로는 DB에서 가져오거나 생성 후 저장해야 함)
        List<String> backupCodes = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            backupCodes.add(generateBackupCode());
        }

        return backupCodes;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminNotificationSettingsDTO getNotificationSettings(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자를 찾을 수 없습니다: " + adminId));

        // 실제로는 DB에서 알림 설정을 가져와야 함
        // 여기서는 기본값으로 설정
        return AdminNotificationSettingsDTO.builder()
                .adminId(admin.getId())
                .emailNotifications(true)
                .pushNotifications(true)
                .smsNotifications(false)
                .orderNotifications(true)
                .stockNotifications(true)
                .memberNotifications(true)
                .reviewNotifications(true)
                .securityNotifications(true)
                .build();
    }

    @Override
    @Transactional
    public AdminNotificationSettingsDTO updateNotificationSettings(AdminNotificationSettingsDTO settingsDTO) {
        Admin admin = adminRepository.findById(settingsDTO.getAdminId())
                .orElseThrow(() -> new EntityNotFoundException("관리자를 찾을 수 없습니다: " + settingsDTO.getAdminId()));

        // 실제로는 DB에 알림 설정을 저장해야 함

        // 활동 로그 기록
        ActivityLog activityLog = ActivityLog.builder()
                .type("NOTIFICATION_SETTINGS_UPDATE")
                .description("알림 설정이 업데이트되었습니다.")
                .referenceType("ADMIN")
                .referenceId(admin.getId())
                .userAgent(httpSession.getAttribute("userAgent") != null ? httpSession.getAttribute("userAgent").toString() : "Unknown")
                .ipAddress(httpSession.getAttribute("ipAddress") != null ? httpSession.getAttribute("ipAddress").toString() : "Unknown")
                .createdAt(LocalDateTime.now())
                .userId(admin.getMember().getUserId())
                .build();
        activityLogRepository.save(activityLog);

        return settingsDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminSession> getAdminSessions(Long adminId) {
        return adminSessionRepository.findActiveSessionsByAdminId(adminId, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void terminateSession(Long sessionId) {
        // 세션 종료 (만료 시간 설정)
        adminSessionRepository.expireSession(sessionId, LocalDateTime.now());

        // 활동 로그 기록
        ActivityLog activityLog = ActivityLog.builder()
                .type("SESSION_TERMINATE")
                .description("세션이 종료되었습니다.")
                .referenceType("SESSION")
                .referenceId(sessionId)
                .userAgent(httpSession.getAttribute("userAgent") != null ? httpSession.getAttribute("userAgent").toString() : "Unknown")
                .ipAddress(httpSession.getAttribute("ipAddress") != null ? httpSession.getAttribute("ipAddress").toString() : "Unknown")
                .createdAt(LocalDateTime.now())
                .build();
        activityLogRepository.save(activityLog);
    }

    @Override
    @Transactional
    public void terminateAllSessionsExcept(Long currentSessionId) {
        // 현재 로그인한 관리자 정보 가져오기
        Admin admin = getCurrentAdmin();

        // 현재 세션을 제외한 모든 세션 종료
        adminSessionRepository.expireAllSessionsExcept(admin.getId(), currentSessionId, LocalDateTime.now());

        // 활동 로그 기록
        ActivityLog activityLog = ActivityLog.builder()
                .type("ALL_SESSIONS_TERMINATE")
                .description("모든 다른 세션이 종료되었습니다.")
                .referenceType("ADMIN")
                .referenceId(admin.getId())
                .userAgent(httpSession.getAttribute("userAgent") != null ? httpSession.getAttribute("userAgent").toString() : "Unknown")
                .ipAddress(httpSession.getAttribute("ipAddress") != null ? httpSession.getAttribute("ipAddress").toString() : "Unknown")
                .createdAt(LocalDateTime.now())
                .userId(admin.getMember().getUserId())
                .build();
        activityLogRepository.save(activityLog);
    }

    // 비밀번호 복잡성 검증
    private void validatePasswordComplexity(String password) {
        if (password.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다.");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("비밀번호는 최소 하나의 대문자를 포함해야 합니다.");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("비밀번호는 최소 하나의 소문자를 포함해야 합니다.");
        }

        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("비밀번호는 최소 하나의 숫자를 포함해야 합니다.");
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new IllegalArgumentException("비밀번호는 최소 하나의 특수문자를 포함해야 합니다.");
        }
    }

    // 2단계 인증 비밀키 생성
    private String generateTwoFactorSecret() {
        // 실제로는 TOTP 라이브러리를 사용하여 비밀키 생성
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16).toUpperCase();
    }

    // 2단계 인증 QR 코드 URL 생성 (userId 사용)
    private String generateTwoFactorQrCodeUrl(String userId, String secret) {
        // 실제로는 TOTP 라이브러리를 사용하여 QR 코드 URL 생성
        return "https://chart.googleapis.com/chart?chs=200x200&chld=M|0&cht=qr&chl=otpauth://totp/PeakMeShop:" + userId + "?secret=" + secret + "&issuer=PeakMeShop";
    }

    // 2단계 인증 코드 검증
    private boolean validateTwoFactorAuthCode(String secret, String authCode) {
        // 실제로는 TOTP 라이브러리를 사용하여 인증 코드 검증
        return authCode != null && authCode.length() == 6 && authCode.matches("\\d+");
    }

    // 백업 코드 생성
    private String generateBackupCode() {
        // 실제로는 보다 안전한 방식으로 생성해야 함
        return String.format("%04d-%04d-%04d",
                (int) (Math.random() * 10000),
                (int) (Math.random() * 10000),
                (int) (Math.random() * 10000));
    }
}