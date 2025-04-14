package com.peakmeshop.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.peakmeshop.domain.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Page<Notice> findByCategoryAndStatus(String category, String status, Pageable pageable);
} 