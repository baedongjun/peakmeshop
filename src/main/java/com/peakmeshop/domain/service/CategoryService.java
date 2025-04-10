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
}