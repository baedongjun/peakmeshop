package com.peakmeshop.domain.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.peakmeshop.domain.repository.OrderItemRepository;
import jakarta.persistence.EntityNotFoundException;
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
import com.peakmeshop.api.mapper.CategoryMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getCategorySalesData() {
        // 카테고리별 판매 비율 데이터를 조회합니다.
        // 반환되는 배열의 각 요소는 [카테고리명, 판매 비율]을 포함합니다.
        return orderItemRepository.findCategorySalesPercentage();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getCategoryProductCounts() {
        // 카테고리별 상품 수를 조회합니다.
        return categoryRepository.findCategoryProductCounts();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getCategoryRevenue() {
        // 카테고리별 매출 금액을 조회합니다.
        return orderItemRepository.findCategoryRevenue();
    }

    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        // 부모 카테고리 확인
        Category parent = null;
        if (categoryDTO.getParentId() != null) {
            parent = categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent category not found with id: " + categoryDTO.getParentId()));
        }

        // 카테고리 생성
        Category category = categoryMapper.toEntity(categoryDTO);
        category.setParent(parent);
        category.setCreatedAt(LocalDateTime.now());

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        return categoryMapper.toDTO(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryDTO> getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .map(categoryMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryDTO> getCategories(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories.map(categoryMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getActiveCategories() {
        List<Category> categories = categoryRepository.findByIsActiveTrue();
        return categories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getRootCategories() {
        List<Category> categories = categoryRepository.findByParentIsNull();
        return categories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getChildCategories(Long parentId) {
        List<Category> categories = categoryRepository.findByParentId(parentId);
        return categories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getCategoriesByParentId(Long parentId) {
        List<Category> categories = categoryRepository.findByParentId(parentId);
        return categories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        // 부모 카테고리 확인
        Category parent = null;
        if (categoryDTO.getParentId() != null) {
            // 자기 자신을 부모로 설정하는 것 방지
            if (categoryDTO.getParentId().equals(id)) {
                throw new BadRequestException("Category cannot be its own parent");
            }

            parent = categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent category not found with id: " + categoryDTO.getParentId()));

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
        Category updatedCategory = categoryMapper.toEntity(categoryDTO);
        category.setName(updatedCategory.getName());
        category.setDescription(updatedCategory.getDescription());
        category.setSlug(updatedCategory.getSlug());
        category.setParent(parent);
        category.setImageUrl(updatedCategory.getImageUrl());
        category.setActive(updatedCategory.isActive());
        category.setFeatured(updatedCategory.isFeatured());
        category.setSortOrder(updatedCategory.getSortOrder());
        category.setFilterableAttributes(updatedCategory.getFilterableAttributes());
        category.setUpdatedAt(LocalDateTime.now());

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(savedCategory);
    }

    @Override
    @Transactional
    public boolean deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

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
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        // 필터링 가능한 속성 업데이트
        if (attributes != null && !attributes.isEmpty()) {
            category.setFilterableAttributes(attributes);
        } else {
            category.setFilterableAttributes(null);
        }

        category.setUpdatedAt(LocalDateTime.now());

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategoryStatus(Long id, boolean active) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        category.setActive(active);
        category.setUpdatedAt(LocalDateTime.now());

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategoryPosition(Long id, int position) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        category.setSortOrder(position);
        category.setUpdatedAt(LocalDateTime.now());

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategoryFeatured(Long id, boolean featured) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        category.setFeatured(featured);
        category.setUpdatedAt(LocalDateTime.now());

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getFeaturedCategories() {
        List<Category> categories = categoryRepository.findByIsFeaturedTrue();
        return categories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }
}