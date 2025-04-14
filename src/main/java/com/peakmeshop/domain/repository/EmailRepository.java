package com.peakmeshop.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.peakmeshop.domain.entity.Email;

public interface EmailRepository extends JpaRepository<Email, Long> {
    Page<Email> findByStatus(String status, Pageable pageable);
} 