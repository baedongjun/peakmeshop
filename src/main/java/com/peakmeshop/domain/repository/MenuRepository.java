package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    
    List<Menu> findAllByType(String type);
    
    List<Menu> findByIsActiveTrueOrderBySortOrderAsc();
    
    @Modifying
    @Query("UPDATE Menu m SET m.sortOrder = m.sortOrder - 1 WHERE m.sortOrder BETWEEN :start AND :end")
    void decreaseOrderBetween(@Param("start") int start, @Param("end") int end);
    
    @Modifying
    @Query("UPDATE Menu m SET m.sortOrder = m.sortOrder + 1 WHERE m.sortOrder BETWEEN :start AND :end")
    void increaseOrderBetween(@Param("start") int start, @Param("end") int end);
} 