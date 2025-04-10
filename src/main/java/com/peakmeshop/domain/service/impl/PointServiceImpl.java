package com.peakmeshop.domain.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.PointDTO;
import com.peakmeshop.api.dto.PointHistoryDTO;
import com.peakmeshop.api.dto.PointSummaryDTO;
import com.peakmeshop.domain.entity.Member;
import com.peakmeshop.domain.entity.Point;
import com.peakmeshop.domain.entity.PointHistory;
import com.peakmeshop.common.exception.BadRequestException;
import com.peakmeshop.domain.repository.MemberRepository;
import com.peakmeshop.domain.repository.PointHistoryRepository;
import com.peakmeshop.domain.repository.PointRepository;
import com.peakmeshop.domain.service.PointService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointServiceImpl implements PointService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public PointDTO getMemberPoint(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found with id: " + memberId));

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
                .orElseThrow(() -> new UsernameNotFoundException("Member not found with id: " + memberId));

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
                .orElseThrow(() -> new UsernameNotFoundException("Member not found with id: " + memberId));

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
                .orElseThrow(() -> new UsernameNotFoundException("Member not found with id: " + memberId));

        Point point = pointRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("Point not found for member id: " + memberId));

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
                .orElseThrow(() -> new UsernameNotFoundException("Member not found with id: " + memberId));

        Point point = pointRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("Point not found for member id: " + memberId));

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

    @Override
    @Transactional
    public PointDTO earnPoints(Long memberId, Long amount, String reason) {
        if (amount <= 0) {
            throw new BadRequestException("적립할 포인트는 0보다 커야 합니다.");
        }

        // 회원 존재 여부 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));

        // 포인트 적립 (기본 유효기간 1년)
        Point point = Point.builder()
                .member(member)
                .amount(amount)
                .reason(reason)
                .type("EARN")
                .expiryDate(LocalDateTime.now().plusYears(1))
                .createdAt(LocalDateTime.now())
                .build();

        Point savedPoint = pointRepository.save(point);
        return convertToDTO(savedPoint);
    }

    @Override
    @Transactional
    public PointDTO usePoints(Long memberId, Long amount, String reason) {
        if (amount <= 0) {
            throw new BadRequestException("사용할 포인트는 0보다 커야 합니다.");
        }

        // 회원의 가용 포인트 확인
        int availablePoints = getMemberTotalPoints(memberId);
        if (availablePoints < amount) {
            throw new BadRequestException("사용 가능한 포인트가 부족합니다.");
        }

        // 회원 존재 여부 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));


        // 포인트 사용
        Point point = Point.builder()
                .member(member)
                .amount(-amount)
                .reason(reason)
                .type("USE")
                .createdAt(LocalDateTime.now())
                .build();

        Point savedPoint = pointRepository.save(point);
        return convertToDTO(savedPoint);
    }

    @Override
    @Transactional
    public PointDTO refundPoints(Long memberId, Long amount, String reason) {
        if (amount <= 0) {
            throw new BadRequestException("환불할 포인트는 0보다 커야 합니다.");
        }

        // 회원 존재 여부 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));


        // 포인트 환불
        Point point = Point.builder()
                .member(member)
                .amount(amount)
                .reason(reason)
                .type("REFUND")
                .createdAt(LocalDateTime.now())
                .build();

        Point savedPoint = pointRepository.save(point);
        return convertToDTO(savedPoint);
    }

    @Override
    public int getMemberTotalPoints(Long memberId) {
        return pointRepository.getTotalPointsByMemberId(memberId, LocalDateTime.now());
    }

    @Override
    public List<PointDTO> getExpiringPoints(LocalDateTime start, LocalDateTime end) {
        List<Point> points = pointRepository.findExpiringPoints(start, end);
        return points.stream().map(this::convertToDTO).toList();
    }

    @Override
    public PointSummaryDTO getPointSummary(String period, String startDate, String endDate) {
        LocalDateTime start;
        LocalDateTime end;

        // 기간 설정
        if (startDate != null && endDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            start = LocalDate.parse(startDate, formatter).atStartOfDay();
            end = LocalDate.parse(endDate, formatter).plusDays(1).atStartOfDay();
        } else {
            LocalDate now = LocalDate.now();
            switch (period) {
                case "daily":
                    start = now.atStartOfDay();
                    end = now.plusDays(1).atStartOfDay();
                    break;
                case "weekly":
                    start = now.minusWeeks(1).atStartOfDay();
                    end = now.plusDays(1).atStartOfDay();
                    break;
                case "monthly":
                    start = now.minusMonths(1).atStartOfDay();
                    end = now.plusDays(1).atStartOfDay();
                    break;
                case "yearly":
                    start = now.minusYears(1).atStartOfDay();
                    end = now.plusDays(1).atStartOfDay();
                    break;
                default:
                    start = now.minusMonths(1).atStartOfDay();
                    end = now.plusDays(1).atStartOfDay();
            }
        }

        // 포인트 통계 데이터 조회
        Object[] earnedAndUsed = pointRepository.getEarnedAndUsedPoints(start, end);
        List<Object[]> pointsByType = pointRepository.getPointStatisticsByType();
        List<Object[]> dailyStats = pointRepository.getPointStatistics(start, end);

        // 통계 데이터 변환
        List<Map<String, Object>> pointsByTypeList = new ArrayList<>();
        for (Object[] row : pointsByType) {
            Map<String, Object> map = new HashMap<>();
            map.put("type", row[0]);
            map.put("count", ((Number) row[1]).longValue());
            map.put("total", ((Number) row[2]).longValue());
            pointsByTypeList.add(map);
        }

        List<Map<String, Object>> dailyStatsList = new ArrayList<>();
        for (Object[] row : dailyStats) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", row[0]);
            map.put("count", ((Number) row[1]).longValue());
            map.put("total", ((Number) row[2]).longValue());
            dailyStatsList.add(map);
        }

        // DTO 생성
        return PointSummaryDTO.builder()
                .totalPoints(getMemberTotalPoints(null))
                .totalEarnedPoints(((Number) earnedAndUsed[0]).longValue())
                .totalUsedPoints(((Number) earnedAndUsed[1]).longValue())
                .monthlyEarnedPoints(0L) // TODO: 구현 필요
                .monthlyUsedPoints(0L)   // TODO: 구현 필요
                .dailyEarnedPoints(0L)   // TODO: 구현 필요
                .dailyUsedPoints(0L)     // TODO: 구현 필요
                .averagePointsPerMember(0.0) // TODO: 구현 필요
                .pointsByType(pointsByTypeList)
                .dailyStatistics(dailyStatsList)
                .totalExpiredPoints(0L).build();
    }

    @Override
    @Transactional
    public void expirePoints() {
        // 만료된 포인트 처리
        List<Point> expiredPoints = pointRepository.findExpiringPoints(
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now());

        for (Point point : expiredPoints) {
            point.setType("EXPIRED");
            pointRepository.save(point);
        }
    }

    @Override
    public Page<PointDTO> getPointTransactions(String type, LocalDateTime startDate,
                                             LocalDateTime endDate, Pageable pageable) {
        // TODO: 구현 필요
        return Page.empty();
    }

    private PointDTO convertToDTO(Point point) {
        return PointDTO.builder()
                .id(point.getId())
                .member(point.getMember())
                .amount(point.getAmount())
                .reason(point.getReason())
                .type(point.getType())
                .expiryDate(point.getExpiryDate())
                .createdAt(point.getCreatedAt())
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