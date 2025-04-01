package com.peakmeshop.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.peakmeshop.domain.entity.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    Page<Banner> findByStartDateBeforeAndEndDateAfterAndActiveTrue(LocalDateTime now, LocalDateTime now2, Pageable pageable);

    List<Banner> findByStartDateBeforeAndEndDateAfterAndActiveTrue(LocalDateTime now, LocalDateTime now2);

    List<Banner> findByPositionAndStartDateBeforeAndEndDateAfterAndActiveTrue(String position, LocalDateTime now, LocalDateTime now2);
}