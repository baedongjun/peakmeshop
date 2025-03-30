package com.peakmeshop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.entity.PointHistory;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    Page<PointHistory> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
}