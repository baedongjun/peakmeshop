package com.peakmeshop.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.domain.entity.Qna;

public interface QnaRepository extends JpaRepository<Qna, Long> {
    Long countByProductId(Long productId);

    Page<Qna> findByProductId(Long productId, Pageable pageable);

    Page<Qna> findByMemberId(Long memberId, Pageable pageable);

    Page<Qna> findByStatus(String status, Pageable pageable);
}