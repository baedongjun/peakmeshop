package com.peakmeshop.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.entity.Banner;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    Page<Banner> findByStartDateBeforeAndEndDateAfterAndActiveTrue(LocalDateTime now, LocalDateTime now2, Pageable pageable);

    List<Banner> findByStartDateBeforeAndEndDateAfterAndActiveTrue(LocalDateTime now, LocalDateTime now2);

    List<Banner> findByPositionAndStartDateBeforeAndEndDateAfterAndActiveTrue(String position, LocalDateTime now, LocalDateTime now2);
}