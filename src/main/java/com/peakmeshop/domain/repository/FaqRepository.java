package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.Faq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FaqRepository extends JpaRepository<Faq, Long> {
    
    Page<Faq> findByCategory(String category, Pageable pageable);
    
    @Query("SELECT MAX(f.sortOrder) FROM Faq f")
    Integer findMaxSortOrder();
    
    @Modifying
    @Query("UPDATE Faq f SET f.sortOrder = f.sortOrder - 1 WHERE f.sortOrder BETWEEN :start AND :end")
    void decreaseOrderBetween(@Param("start") int start, @Param("end") int end);
    
    @Modifying
    @Query("UPDATE Faq f SET f.sortOrder = f.sortOrder + 1 WHERE f.sortOrder BETWEEN :start AND :end")
    void increaseOrderBetween(@Param("start") int start, @Param("end") int end);
} 