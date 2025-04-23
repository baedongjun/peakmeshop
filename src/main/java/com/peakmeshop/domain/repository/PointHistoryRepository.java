package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.PointHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    Page<PointHistory> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
}