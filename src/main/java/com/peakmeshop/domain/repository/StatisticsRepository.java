package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    
    Optional<Statistics> findByTypeAndDate(String type, LocalDate date);
    
    List<Statistics> findByTypeBetweenDates(String type, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT s FROM Statistics s WHERE s.type = :type AND s.date BETWEEN :startDate AND :endDate")
    List<Statistics> findByTypeAndDateBetween(
            @Param("type") String type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
            
    @Query("SELECT s FROM Statistics s WHERE s.date = :date")
    List<Statistics> findByDate(@Param("date") LocalDate date);
    
    @Query("SELECT s FROM Statistics s WHERE s.type = :type ORDER BY s.date DESC LIMIT 1")
    Optional<Statistics> findLatestByType(@Param("type") String type);
} 