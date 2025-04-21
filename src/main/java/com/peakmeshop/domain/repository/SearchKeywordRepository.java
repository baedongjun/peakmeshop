package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.SearchKeyword;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SearchKeywordRepository extends JpaRepository<SearchKeyword, Long> {

    Optional<SearchKeyword> findByKeyword(String keyword);

    @Query("SELECT k FROM SearchKeyword k ORDER BY k.searchCount DESC")
    Page<SearchKeyword> findPopularKeywords(Pageable pageable);

    @Query("SELECT k FROM SearchKeyword k WHERE LOWER(k.keyword) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<SearchKeyword> findSimilarKeywords(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT k.keyword FROM SearchKeyword k ORDER BY k.searchCount DESC")
    List<String> findTop10ByOrderBySearchCountDesc();

    @Query("SELECT k.keyword FROM SearchKeyword k WHERE k.createdAt >= :startDate AND k.createdAt <= :endDate ORDER BY k.searchCount DESC")
    List<String> findTop10ByCreatedAtBetweenOrderBySearchCountDesc(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT k.keyword FROM SearchKeyword k WHERE LOWER(k.keyword) LIKE LOWER(CONCAT(:prefix, '%')) ORDER BY k.searchCount DESC")
    List<String> findTop10ByKeywordStartingWith(@Param("prefix") String prefix);

    Page<SearchKeyword> findByKeywordContaining(String keyword, Pageable pageable);
} 