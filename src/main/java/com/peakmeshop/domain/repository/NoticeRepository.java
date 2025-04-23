package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Page<Notice> findAllByOrderByImportantDescCreatedAtDesc(Pageable pageable);
    
    List<Notice> findAllByOrderByImportantDescCreatedAtDesc();
    
    List<Notice> findByImportantTrueAndStatusOrderByCreatedAtDesc(String status);
    
    Page<Notice> findByCategoryAndStatusOrderByImportantDescCreatedAtDesc(String category, String status, Pageable pageable);
    
    Page<Notice> findByTitleContainingOrContentContainingOrderByCreatedAtDesc(String title, String content, Pageable pageable);
    
    Page<Notice> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    List<Notice> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
    
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    long countByImportantTrueAndCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    Page<Notice> findByCategoryAndStatus(String category, String status, Pageable pageable);

    long countByStatusAndCreatedAtBetween(String status, LocalDateTime start, LocalDateTime end);

    @Query("SELECT AVG(n.viewCount) FROM Notice n WHERE n.createdAt BETWEEN :start AND :end")
    Double getAverageViewCountByPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
} 