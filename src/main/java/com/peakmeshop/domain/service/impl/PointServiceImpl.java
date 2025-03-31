package com.peakmeshop.domain.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.PointDTO;
import com.peakmeshop.api.dto.PointHistoryDTO;
import com.peakmeshop.domain.entity.Member;
import com.peakmeshop.domain.entity.Point;
import com.peakmeshop.domain.entity.PointHistory;
import com.peakmeshop.common.exception.BadRequestException;
import com.peakmeshop.common.exception.ResourceNotFoundException;
import com.peakmeshop.domain.repository.MemberRepository;
import com.peakmeshop.domain.repository.PointHistoryRepository;
import com.peakmeshop.domain.repository.PointRepository;
import com.peakmeshop.domain.service.PointService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public PointDTO getMemberPoint(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));

        Point point = pointRepository.findByMemberId(memberId)
                .orElse(Point.builder()
                        .member(member)
                        .currentPoint(0)
                        .totalEarnedPoint(0)
                        .totalUsedPoint(0)
                        .build());

        return convertToDTO(point);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PointHistoryDTO> getMemberPointHistory(Long memberId, Pageable pageable) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));

        Page<PointHistory> pointHistories = pointHistoryRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);
        return pointHistories.map(this::convertToHistoryDTO);
    }

    @Override
    @Transactional
    public PointHistoryDTO addPoint(Long memberId, Integer amount, String reason) {
        if (amount <= 0) {
            throw new BadRequestException("Point amount must be positive");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));

        Point point = pointRepository.findByMemberId(memberId)
                .orElse(Point.builder()
                        .member(member)
                        .currentPoint(0)
                        .totalEarnedPoint(0)
                        .totalUsedPoint(0)
                        .build());

        // 포인트 증가
        point.setCurrentPoint(point.getCurrentPoint() + amount);
        point.setTotalEarnedPoint(point.getTotalEarnedPoint() + amount);
        point.setUpdatedAt(LocalDateTime.now());

        Point savedPoint = pointRepository.save(point);

        // 포인트 내역 저장
        PointHistory pointHistory = PointHistory.builder()
                .member(member)
                .amount(amount)
                .type(PointHistory.TYPE_EARN)
                .reason(reason)
                .balanceAfter(savedPoint.getCurrentPoint())
                .createdAt(LocalDateTime.now())
                .build();

        PointHistory savedHistory = pointHistoryRepository.save(pointHistory);

        return convertToHistoryDTO(savedHistory);
    }

    @Override
    @Transactional
    public PointHistoryDTO deductPoint(Long memberId, Integer amount, String reason) {
        if (amount <= 0) {
            throw new BadRequestException("Point amount must be positive");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));

        Point point = pointRepository.findByMemberId(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Point not found for member id: " + memberId));

        // 포인트 차감
        point.setCurrentPoint(point.getCurrentPoint() - amount);
        point.setUpdatedAt(LocalDateTime.now());

        Point savedPoint = pointRepository.save(point);

        // 포인트 내역 저장
        PointHistory pointHistory = PointHistory.builder()
                .member(member)
                .amount(amount)
                .type(PointHistory.TYPE_DEDUCT)
                .reason(reason)
                .balanceAfter(savedPoint.getCurrentPoint())
                .createdAt(LocalDateTime.now())
                .build();

        PointHistory savedHistory = pointHistoryRepository.save(pointHistory);

        return convertToHistoryDTO(savedHistory);
    }

    @Override
    @Transactional
    public PointHistoryDTO usePoint(Long memberId, Integer amount, String reason) {
        if (amount <= 0) {
            throw new BadRequestException("Point amount must be positive");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));

        Point point = pointRepository.findByMemberId(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Point not found for member id: " + memberId));

        // 사용 가능한 포인트 확인
        if (point.getCurrentPoint() < amount) {
            throw new BadRequestException("Not enough points. Current: " + point.getCurrentPoint() + ", Requested: " + amount);
        }

        // 포인트 사용
        point.setCurrentPoint(point.getCurrentPoint() - amount);
        point.setTotalUsedPoint(point.getTotalUsedPoint() + amount);
        point.setUpdatedAt(LocalDateTime.now());

        Point savedPoint = pointRepository.save(point);

        // 포인트 내역 저장
        PointHistory pointHistory = PointHistory.builder()
                .member(member)
                .amount(amount)
                .type(PointHistory.TYPE_USE)
                .reason(reason)
                .balanceAfter(savedPoint.getCurrentPoint())
                .createdAt(LocalDateTime.now())
                .build();

        PointHistory savedHistory = pointHistoryRepository.save(pointHistory);

        return convertToHistoryDTO(savedHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PointDTO> getAllMemberPoints() {
        List<Point> points = pointRepository.findAll();
        return points.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PointDTO convertToDTO(Point point) {
        return PointDTO.builder()
                .id(point.getId())
                .memberId(point.getMember().getId())
                .memberName(point.getMember().getName())
                .memberEmail(point.getMember().getEmail())
                .currentPoint(point.getCurrentPoint())
                .totalEarnedPoint(point.getTotalEarnedPoint())
                .totalUsedPoint(point.getTotalUsedPoint())
                .updatedAt(point.getUpdatedAt())
                .build();
    }

    private PointHistoryDTO convertToHistoryDTO(PointHistory history) {
        return PointHistoryDTO.builder()
                .id(history.getId())
                .memberId(history.getMember().getId())
                .memberName(history.getMember().getName())
                .amount(history.getAmount())
                .type(history.getType())
                .reason(history.getReason())
                .balanceAfter(history.getBalanceAfter())
                .createdAt(history.getCreatedAt())
                .orderId(history.getOrderId())
                .build();
    }
}