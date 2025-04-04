package com.peakmeshop.domain.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.PointDTO;
import com.peakmeshop.api.dto.PointHistoryDTO;

public interface PointService {

    PointDTO getMemberPoint(Long memberId);

    Page<PointHistoryDTO> getMemberPointHistory(Long memberId, Pageable pageable);

    PointHistoryDTO addPoint(Long memberId, Integer amount, String reason);

    PointHistoryDTO deductPoint(Long memberId, Integer amount, String reason);

    PointHistoryDTO usePoint(Long memberId, Integer amount, String reason);

    List<PointDTO> getAllMemberPoints();
}