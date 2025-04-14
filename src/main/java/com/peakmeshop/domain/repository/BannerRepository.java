package com.peakmeshop.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.peakmeshop.domain.entity.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

    Page<Banner> findByStartDateBeforeAndEndDateAfterAndIsActiveTrue(LocalDateTime now, LocalDateTime now2, Pageable pageable);

    List<Banner> findByStartDateBeforeAndEndDateAfterAndIsActiveTrue(LocalDateTime now, LocalDateTime now2);

    List<Banner> findByPositionAndStartDateBeforeAndEndDateAfterAndIsActiveTrue(String position, LocalDateTime now, LocalDateTime now2);

    Page<Banner> findByStatus(Banner.BannerStatus status, Pageable pageable);

    List<Banner> findByStatusOrderBySortOrderAsc(Banner.BannerStatus status);

    @Modifying
    @Query("UPDATE Banner b SET b.sortOrder = b.sortOrder - 1 WHERE b.sortOrder BETWEEN :start AND :end")
    void decreaseOrderBetween(@Param("start") int start, @Param("end") int end);

    @Modifying
    @Query("UPDATE Banner b SET b.sortOrder = b.sortOrder + 1 WHERE b.sortOrder BETWEEN :start AND :end")
    void increaseOrderBetween(@Param("start") int start, @Param("end") int end);
}