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

    @Query("SELECT t FROM Tag t WHERE SIZE(t.products) > 0")
    List<Tag> findTagsWithProducts();

    @Query("SELECT t FROM Tag t WHERE t.name LIKE %:keyword% OR t.description LIKE %:keyword%")
    List<Tag> searchTags(@Param("keyword") String keyword);

    @Query(value = "SELECT t.* FROM tags t " +
            "JOIN product_tags pt ON t.id = pt.tag_id " +
            "GROUP BY t.id " +
            "ORDER BY COUNT(pt.product_id) DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<Tag> findPopularTags(@Param("limit") int limit);
}