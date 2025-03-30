package com.peakmeshop.controller;

import java.util.HashMap;
import java.util.Map;

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
import com.peakmeshop.dto.MemberDTO;
import com.peakmeshop.dto.MemberUpdateDTO;
import com.peakmeshop.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/members")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class AdminMemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<Page<MemberDTO>> getAllMembers(@PageableDefault(size = 10) Pageable pageable) {
        Page<MemberDTO> members = memberService.getAllMembers(pageable);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MemberDTO>> searchMembers(@RequestParam String keyword, @PageableDefault(size = 10) Pageable pageable) {
        // List<MemberDTO>를 Page<MemberDTO>로 변환
        Page<MemberDTO> members = memberService.getAllMembers(pageable);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable Long id) {
        MemberDTO member = memberService.getMemberById(id);
        return ResponseEntity.ok(member);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getMemberCount() {
        long count = memberService.countMembers();
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<MemberDTO> updateMemberStatus(@PathVariable Long id, @RequestParam String status) {
        MemberDTO updatedMember = memberService.updateMemberStatus(id, status);
        return ResponseEntity.ok(updatedMember);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberDTO> updateMember(@PathVariable Long id, @RequestBody MemberDTO memberDTO) {
        MemberDTO updatedMember = memberService.updateMember(id, memberDTO);
        return ResponseEntity.ok(updatedMember);
    }

    @PostMapping
    public ResponseEntity<MemberDTO> createMember(@RequestBody MemberDTO memberDTO) {
        MemberDTO createdMember = memberService.createMember(memberDTO);
        return new ResponseEntity<>(createdMember, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.ok(new ApiResponse(true, "회원이 삭제되었습니다."));
    }
}