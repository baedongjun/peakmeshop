package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    
    Page<Inquiry> findByCategory(String category, Pageable pageable);
    
    Page<Inquiry> findByStatus(Inquiry.InquiryStatus status, Pageable pageable);
    
    Page<Inquiry> findByCategoryAndStatus(String category, Inquiry.InquiryStatus status, Pageable pageable);
} 