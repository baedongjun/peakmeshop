package com.peakmeshop.api.controller;

import java.util.HashMap;
import java.util.Map;

import com.peakmeshop.api.dto.AdminProfileDTO;
import com.peakmeshop.api.dto.ApiResponse;
import com.peakmeshop.api.dto.MemberDTO;
import com.peakmeshop.common.annotation.LogActivity;
import com.peakmeshop.domain.service.AdminService;
import com.peakmeshop.domain.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;

    @PutMapping
    @LogActivity(
            type = "API_PROFILE_UPDATE",
            description = "API를 통한 관리자 프로필 업데이트",
            referenceType = "ADMIN_API"
    )
    public ResponseEntity<?> updateProfile(@RequestBody AdminProfileDTO profileDTO) {
        return ResponseEntity.ok(adminService.updateProfile(profileDTO));
    }

    @PostMapping("/password")
    @LogActivity(
            type = "API_PASSWORD_CHANGE",
            description = "API를 통한 관리자 비밀번호 변경",
            referenceType = "ADMIN_API"
    )
    public ResponseEntity<?> changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword) {
        return ResponseEntity.ok(adminService.changePassword(currentPassword, newPassword, confirmPassword));
    }

    @PostMapping("/image")
    @LogActivity(
            type = "API_PROFILE_IMAGE_UPDATE",
            description = "API를 통한 관리자 프로필 이미지 업데이트",
            referenceType = "ADMIN_API"
    )
    public ResponseEntity<?> updateProfileImage(@RequestParam MultipartFile profileImage) {
        return ResponseEntity.ok(adminService.updateProfileImage(profileImage));
    }
}