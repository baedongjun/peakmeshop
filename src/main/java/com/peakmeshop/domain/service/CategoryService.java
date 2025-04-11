package com.peakmeshop.domain.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.CategoryDTO;
import com.peakmeshop.domain.entity.Category;

public interface CategoryService {

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO getCategoryById(Long id);

    Optional<CategoryDTO> getCategoryBySlug(String slug);

    List<CategoryDTO> getAllCategories();

    Page<CategoryDTO> getCategories(Pageable pageable);

    List<CategoryDTO> getActiveCategories();

    List<CategoryDTO> getRootCategories();

    List<CategoryDTO> getChildCategories(Long parentId);

    List<CategoryDTO> getCategoriesByParentId(Long parentId);

    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);

    boolean deleteCategory(Long id);

    CategoryDTO updateCategoryFilterableAttributes(Long id, List<String> attributes);

    CategoryDTO updateCategoryStatus(Long id, boolean active);

    CategoryDTO updateCategoryPosition(Long id, int position);

    CategoryDTO updateCategoryFeatured(Long id, boolean featured);

    List<CategoryDTO> getFeaturedCategories();

    /**
     * 카테고리별 판매 데이터를 조회합니다.
     * 반환되는 배열의 각 요소는 [카테고리명, 판매 비율]을 포함합니다.
     * @return 카테고리별 판매 데이터 목록
     */
    List<Object[]> getCategorySalesData();

    /**
     * 카테고리별 상품 수를 조회합니다.
     * @return 카테고리명과 상품 수를 포함하는 배열의 목록
     */
    List<Object[]> getCategoryProductCounts();

    /**
     * 카테고리별 매출 금액을 조회합니다.
     * @return 카테고리명과 매출 금액을 포함하는 배열의 목록
     */
    List<Object[]> getCategoryRevenue();
}