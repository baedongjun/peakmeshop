package com.peakmeshop.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.domain.entity.Category;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByIsActiveTrue();

    List<Category> findByIsFeaturedTrue();

    Optional<Category> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    int countByParentId(Long parentId);

    /**
     * 카테고리별 상품 수를 조회합니다.
     * @return 카테고리명과 상품 수를 포함하는 배열의 목록
     */
    @Query("SELECT c.name, COUNT(p) FROM Category c LEFT JOIN c.products p GROUP BY c.name")
    List<Object[]> findCategoryProductCounts();

    /**
     * 상위 카테고리를 조회합니다.
     * @return 상위 카테고리 목록
     */
    List<Category> findByParentIsNull();

    /**
     * 특정 상위 카테고리의 하위 카테고리를 조회합니다.
     * @param parentId 상위 카테고리 ID
     * @return 하위 카테고리 목록
     */
    List<Category> findByParentId(Long parentId);

    /**
     * 카테고리명으로 카테고리를 조회합니다.
     * @param name 카테고리명
     * @return 카테고리
     */
    Category findByName(String name);
}