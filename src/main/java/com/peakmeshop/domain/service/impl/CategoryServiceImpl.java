package com.peakmeshop.domain.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.CategoryDTO;
import com.peakmeshop.domain.entity.Category;
import com.peakmeshop.common.exception.BadRequestException;
import com.peakmeshop.domain.repository.CategoryRepository;
import com.peakmeshop.domain.repository.ProductRepository;
import com.peakmeshop.domain.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        // 부모 카테고리 확인
        Category parent = null;
        if (categoryDTO.getParentId() != null) {
            parent = categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new UsernameNotFoundException("Parent category not found with id: " + categoryDTO.getParentId()));
        }

        // 카테고리 생성
        Category category = Category.builder()
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .slug(categoryDTO.getSlug())
                .parent(parent)
                .imageUrl(categoryDTO.getImageUrl())
                .isActive(categoryDTO.isActive())
                .isFeatured(categoryDTO.isFeatured())
                .sortOrder(categoryDTO.getSortOrder())
                .filterableAttributes(categoryDTO.getFilterableAttributes() != null ?
                        String.join(",", categoryDTO.getFilterableAttributes()) : null)
                .createdAt(LocalDateTime.now())
                .build();

        Category savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Category not found with id: " + id));

        return convertToDTO(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryDTO> getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryDTO> getCategories(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getActiveCategories() {
        List<Category> categories = categoryRepository.findByIsActiveTrue();
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getRootCategories() {
        List<Category> categories = categoryRepository.findByParentIsNull();
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getChildCategories(Long parentId) {
        List<Category> categories = categoryRepository.findByParentId(parentId);
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getCategoriesByParentId(Long parentId) {
        List<Category> categories = categoryRepository.findByParentId(parentId);
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Category not found with id: " + id));

        // 부모 카테고리 확인
        Category parent = null;
        if (categoryDTO.getParentId() != null) {
            // 자기 자신을 부모로 설정하는 것 방지
            if (categoryDTO.getParentId().equals(id)) {
                throw new BadRequestException("Category cannot be its own parent");
            }

            parent = categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new UsernameNotFoundException("Parent category not found with id: " + categoryDTO.getParentId()));

            // 순환 참조 방지
            Category current = parent;
            while (current != null) {
                if (current.getId().equals(id)) {
                    throw new BadRequestException("Circular reference detected in category hierarchy");
                }
                current = current.getParent();
            }
        }

        // 카테고리 업데이트
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setSlug(categoryDTO.getSlug());
        category.setParent(parent);
        category.setImageUrl(categoryDTO.getImageUrl());
        category.setActive(categoryDTO.isActive());
        category.setFeatured(categoryDTO.isFeatured());
        category.setSortOrder(categoryDTO.getSortOrder());

        if (categoryDTO.getFilterableAttributes() != null) {
            category.setFilterableAttributes(String.join(",", categoryDTO.getFilterableAttributes()));
        }

        category.setUpdatedAt(LocalDateTime.now());

        Category updatedCategory = categoryRepository.save(category);
        return convertToDTO(updatedCategory);
    }

    @Override
    @Transactional
    public boolean deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Category not found with id: " + id));

        // 하위 카테고리가 있는지 확인
        if (categoryRepository.countByParentId(id) > 0) {
            throw new BadRequestException("Cannot delete category with child categories");
        }

        // 연결된 상품이 있는지 확인
        if (productRepository.countByCategoryId(id) > 0) {
            throw new BadRequestException("Cannot delete category with associated products");
        }

        categoryRepository.delete(category);
        return true;
    }

    @Override
    @Transactional
    public CategoryDTO updateCategoryFilterableAttributes(Long id, List<String> attributes) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Category not found with id: " + id));

        // 필터링 가능한 속성 업데이트
        if (attributes != null && !attributes.isEmpty()) {
            category.setFilterableAttributes(String.join(",", attributes));
        } else {
            category.setFilterableAttributes(null);
        }

        category.setUpdatedAt(LocalDateTime.now());

        Category updatedCategory = categoryRepository.save(category);
        return convertToDTO(updatedCategory);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategoryStatus(Long id, boolean active) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Category not found with id: " + id));

        category.setActive(active);
        category.setUpdatedAt(LocalDateTime.now());

        Category updatedCategory = categoryRepository.save(category);
        return convertToDTO(updatedCategory);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategoryPosition(Long id, int position) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Category not found with id: " + id));

        category.setSortOrder(position);
        category.setUpdatedAt(LocalDateTime.now());

        Category updatedCategory = categoryRepository.save(category);
        return convertToDTO(updatedCategory);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategoryFeatured(Long id, boolean featured) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Category not found with id: " + id));

        category.setFeatured(featured);
        category.setUpdatedAt(LocalDateTime.now());

        Category updatedCategory = categoryRepository.save(category);
        return convertToDTO(updatedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getFeaturedCategories() {
        List<Category> categories = categoryRepository.findByIsFeaturedTrue();
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Category 엔티티를 CategoryDTO로 변환
    private CategoryDTO convertToDTO(Category category) {
        List<String> filterableAttributes = null;
        if (category.getFilterableAttributes() != null && !category.getFilterableAttributes().isEmpty()) {
            filterableAttributes = List.of(category.getFilterableAttributes().split(","));
        }

        // Calculate depth
        int depth = 0;
        Category parent = category.getParent();
        while (parent != null) {
            depth++;
            parent = parent.getParent();
        }

        // Get product count from repository
        long productCount = productRepository.countByCategoryId(category.getId());

        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .slug(category.getSlug())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .imageUrl(category.getImageUrl())
                .isActive(category.isActive())
                .isFeatured(category.isFeatured())
                .sortOrder(category.getSortOrder())
                .filterableAttributes(filterableAttributes)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .productCount(productCount)
                .depth(depth)
                .build();
    }
}