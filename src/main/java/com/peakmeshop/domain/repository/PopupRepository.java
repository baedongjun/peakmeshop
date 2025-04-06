package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.Popup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface PopupRepository extends JpaRepository<Popup, Long> {
    @Query("SELECT p FROM Popup p WHERE p.isActive = true " +
           "AND p.startDateTime <= :now " +
           "AND p.endDateTime >= :now " +
           "ORDER BY p.order ASC")
    List<Popup> findActivePopups(LocalDateTime now);

    List<Popup> findByIsActiveOrderByOrderAsc(Boolean isActive);
} 