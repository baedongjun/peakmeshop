package com.peakmeshop.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.domain.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByParentIsNull();

    List<Category> findByParentId(Long parentId);

    List<Category> findByActiveTrue();

    List<Category> findByFeaturedTrue();

    Optional<Category> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    int countByParentId(Long parentId);
}