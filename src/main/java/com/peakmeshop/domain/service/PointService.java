package com.peakmeshop.domain.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.PointDTO;
import com.peakmeshop.api.dto.PointHistoryDTO;
import com.peakmeshop.api.dto.PointSummaryDTO;

public interface PointService {

    PointDTO getMemberPoint(Long memberId);

    Page<PointHistoryDTO> getMemberPointHistory(Long memberId, Pageable pageable);

    PointHistoryDTO addPoint(Long memberId, Integer amount, String reason);

    PointHistoryDTO deductPoint(Long memberId, Integer amount, String reason);

    PointHistoryDTO usePoint(Long memberId, Integer amount, String reason);

    List<PointDTO> getAllMemberPoints();

    PointDTO earnPoints(Long memberId, Long amount, String reason);

    PointDTO usePoints(Long memberId, Long amount, String reason);

    PointDTO refundPoints(Long memberId, Long amount, String reason);

    int getMemberTotalPoints(Long memberId);

    List<PointDTO> getExpiringPoints(LocalDateTime start, LocalDateTime end);

    PointSummaryDTO getPointSummary(String period, String startDate, String endDate);

    void expirePoints();

    Page<PointDTO> getPointTransactions(String type, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}