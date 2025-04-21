package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.BrandNews;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BrandNewsRepository extends JpaRepository<BrandNews, Long> {

    Page<BrandNews> findByBrandId(Long brandId, Pageable pageable);

    @Modifying
    @Query("UPDATE BrandNews b SET b.viewCount = b.viewCount + 1 WHERE b.id = :newsId")
    void incrementViewCount(@Param("newsId") Long newsId);

    @Query("SELECT b FROM BrandNews b WHERE b.brand.id = :brandId AND b.id < :newsId ORDER BY b.id DESC")
    Optional<BrandNews> findPrevNews(@Param("brandId") Long brandId, @Param("newsId") Long newsId);

    @Query("SELECT b FROM BrandNews b WHERE b.brand.id = :brandId AND b.id > :newsId ORDER BY b.id ASC")
    Optional<BrandNews> findNextNews(@Param("brandId") Long brandId, @Param("newsId") Long newsId);
} 