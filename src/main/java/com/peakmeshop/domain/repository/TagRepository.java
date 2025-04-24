package com.peakmeshop.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peakmeshop.domain.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findBySlug(String slug);

    Optional<Tag> findByName(String name);

    boolean existsBySlug(String slug);

    boolean existsByName(String name);

    @Query(value = "SELECT t FROM Tag t LEFT JOIN t.products p GROUP BY t ORDER BY COUNT(p) DESC LIMIT :limit")
    List<Tag> findPopularTags(@Param("limit") int limit);

    @Query("SELECT t FROM Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Tag> searchTags(@Param("keyword") String keyword);

    @Query("SELECT DISTINCT t FROM Tag t LEFT JOIN FETCH t.products")
    List<Tag> findTagsWithProducts();
}