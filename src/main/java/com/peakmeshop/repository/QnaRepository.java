package com.peakmeshop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.entity.Qna;

public interface QnaRepository extends JpaRepository<Qna, Long> {

    Page<Qna> findByProductId(Long productId, Pageable pageable);

    Page<Qna> findByMemberId(Long memberId, Pageable pageable);

    Page<Qna> findByStatus(String status, Pageable pageable);
}