package com.peakmeshop.controller;

import java.util.List;

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

import com.peakmeshop.dto.ApiResponse;
import com.peakmeshop.dto.MemberTierDTO;
import com.peakmeshop.service.MemberTierService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/member-tiers")
@RequiredArgsConstructor
public class MemberTierController {

    private final MemberTierService memberTierService;

    // 관리자용 API
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MemberTierDTO>> getAllMemberTiers() {
        List<MemberTierDTO> memberTiers = memberTierService.getAllMemberTiers();
        return ResponseEntity.ok(memberTiers);
    }

    @GetMapping("/admin/page")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<MemberTierDTO>> getAllMemberTiersPaged(
            @PageableDefault(size = 10) Pageable pageable) {

        Page<MemberTierDTO> memberTiers = memberTierService.getAllMemberTiers(pageable);
        return ResponseEntity.ok(memberTiers);
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberTierDTO> getMemberTierById(@PathVariable Long id) {
        MemberTierDTO memberTier = memberTierService.getMemberTierById(id);
        return ResponseEntity.ok(memberTier);
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberTierDTO> createMemberTier(@RequestBody MemberTierDTO memberTierDTO) {
        MemberTierDTO createdMemberTier = memberTierService.createMemberTier(memberTierDTO);
        return new ResponseEntity<>(createdMemberTier, HttpStatus.CREATED);
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberTierDTO> updateMemberTier(
            @PathVariable Long id,
            @RequestBody MemberTierDTO memberTierDTO) {

        memberTierDTO.setId(id);
        MemberTierDTO updatedMemberTier = memberTierService.updateMemberTier(id, memberTierDTO);
        return ResponseEntity.ok(updatedMemberTier);
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteMemberTier(@PathVariable Long id) {
        memberTierService.deleteMemberTier(id);
        return ResponseEntity.ok(new ApiResponse(true, "회원 등급이 성공적으로 삭제되었습니다."));
    }

    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberTierDTO> toggleTierStatus(
            @PathVariable Long id,
            @RequestParam Boolean active) {

        MemberTierDTO updatedMemberTier = memberTierService.toggleTierStatus(id, active);
        return ResponseEntity.ok(updatedMemberTier);
    }

    @PutMapping("/admin/order")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateTierOrder(@RequestBody List<MemberTierDTO> tierOrders) {
        int updatedCount = memberTierService.updateTierOrder(tierOrders);
        return ResponseEntity.ok(new ApiResponse(true, updatedCount + "개의 회원 등급 순서가 업데이트되었습니다."));
    }

    // 사용자용 API
    @GetMapping
    public ResponseEntity<List<MemberTierDTO>> getActiveMemberTiers() {
        List<MemberTierDTO> memberTiers = memberTierService.getActiveMemberTiers();
        return ResponseEntity.ok(memberTiers);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<MemberTierDTO> getMemberTierByCode(@PathVariable String code) {
        MemberTierDTO memberTier = memberTierService.getMemberTierByCode(code);
        return ResponseEntity.ok(memberTier);
    }
}