package com.peakmeshop.api.controller;

import com.peakmeshop.api.dto.PointDTO;
import com.peakmeshop.api.dto.PointHistoryDTO;
import com.peakmeshop.api.dto.PointRequestDTO;
import com.peakmeshop.common.security.oauth2.user.UserPrincipal;
import com.peakmeshop.domain.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @GetMapping("/member/{memberId}")
    @PreAuthorize("hasRole('ADMIN') or #memberId == authentication.principal.id")
    public ResponseEntity<PointDTO> getMemberPoint(@PathVariable Long memberId) {
        PointDTO point = pointService.getMemberPoint(memberId);
        return ResponseEntity.ok(point);
    }

    @GetMapping("/my-point")
    public ResponseEntity<PointDTO> getMyPoint(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        PointDTO point = pointService.getMemberPoint(userPrincipal.getId());
        return ResponseEntity.ok(point);
    }

    @GetMapping("/history/member/{memberId}")
    @PreAuthorize("hasRole('ADMIN') or #memberId == authentication.principal.id")
    public ResponseEntity<Page<PointHistoryDTO>> getMemberPointHistory(
            @PathVariable Long memberId, Pageable pageable) {

        Page<PointHistoryDTO> pointHistory = pointService.getMemberPointHistory(memberId, pageable);
        return ResponseEntity.ok(pointHistory);
    }

    @GetMapping("/history/my-point")
    public ResponseEntity<Page<PointHistoryDTO>> getMyPointHistory(
            @AuthenticationPrincipal UserPrincipal userPrincipal, Pageable pageable) {

        Page<PointHistoryDTO> pointHistory = pointService.getMemberPointHistory(userPrincipal.getId(), pageable);
        return ResponseEntity.ok(pointHistory);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PointHistoryDTO> addPoint(@RequestBody PointRequestDTO pointRequestDTO) {
        PointHistoryDTO pointHistory = pointService.addPoint(
                pointRequestDTO.getMemberId(),
                pointRequestDTO.getAmount(),
                pointRequestDTO.getReason());

        return ResponseEntity.status(HttpStatus.CREATED).body(pointHistory);
    }

    @PostMapping("/deduct")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PointHistoryDTO> deductPoint(@RequestBody PointRequestDTO pointRequestDTO) {
        PointHistoryDTO pointHistory = pointService.deductPoint(
                pointRequestDTO.getMemberId(),
                pointRequestDTO.getAmount(),
                pointRequestDTO.getReason());

        return ResponseEntity.status(HttpStatus.CREATED).body(pointHistory);
    }

    @PostMapping("/use")
    public ResponseEntity<PointHistoryDTO> usePoint(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody PointRequestDTO pointRequestDTO) {

        // 관리자가 아닌 경우 자신의 포인트만 사용 가능
        if (!userPrincipal.isAdmin() && !pointRequestDTO.getMemberId().equals(userPrincipal.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        PointHistoryDTO pointHistory = pointService.usePoint(
                pointRequestDTO.getMemberId(),
                pointRequestDTO.getAmount(),
                pointRequestDTO.getReason());

        return ResponseEntity.status(HttpStatus.CREATED).body(pointHistory);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PointDTO>> getAllMemberPoints() {
        List<PointDTO> points = pointService.getAllMemberPoints();
        return ResponseEntity.ok(points);
    }
}