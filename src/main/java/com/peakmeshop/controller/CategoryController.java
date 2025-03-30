package com.peakmeshop.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peakmeshop.dto.CategoryDTO;
import com.peakmeshop.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping
    public ResponseEntity<Page<CategoryDTO>> getAllCategories(Pageable pageable) {
        Page<CategoryDTO> categories = categoryService.getAllCategoriesPaged(pageable);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryDTO>> getAllCategoriesWithoutPaging() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<CategoryDTO> getCategoryBySlug(@PathVariable String slug) {
        return categoryService.getCategoryBySlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<CategoryDTO>> getCategoriesByParentId(@PathVariable Long parentId) {
        List<CategoryDTO> categories = categoryService.getCategoriesByParentId(parentId);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/root")
    public ResponseEntity<List<CategoryDTO>> getRootCategories() {
        List<CategoryDTO> categories = categoryService.getRootCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/active")
    public ResponseEntity<List<CategoryDTO>> getActiveCategories() {
        List<CategoryDTO> categories = categoryService.getActiveCategories();
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteCategory(@PathVariable Long id) {
        boolean deleted = categoryService.deleteCategory(id);
        return ResponseEntity.ok(Map.of("deleted", deleted));
    }

    @PutMapping("/{id}/attributes")
    public ResponseEntity<CategoryDTO> updateCategoryFilterableAttributes(
            @PathVariable Long id, @RequestBody List<String> attributes) {
        CategoryDTO updatedCategory = categoryService.updateCategoryFilterableAttributes(id, attributes);
        return ResponseEntity.ok(updatedCategory);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<CategoryDTO> updateCategoryStatus(
            @PathVariable Long id, @RequestParam boolean active) {
        CategoryDTO updatedCategory = categoryService.updateCategoryStatus(id, active);
        return ResponseEntity.ok(updatedCategory);
    }

    @PutMapping("/{id}/position")
    public ResponseEntity<CategoryDTO> updateCategoryPosition(
            @PathVariable Long id, @RequestParam int position) {
        CategoryDTO updatedCategory = categoryService.updateCategoryPosition(id, position);
        return ResponseEntity.ok(updatedCategory);
    }

    @PutMapping("/{id}/featured")
    public ResponseEntity<CategoryDTO> updateCategoryFeatured(
            @PathVariable Long id, @RequestParam boolean featured) {
        CategoryDTO updatedCategory = categoryService.updateCategoryFeatured(id, featured);
        return ResponseEntity.ok(updatedCategory);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<CategoryDTO>> getFeaturedCategories() {
        List<CategoryDTO> categories = categoryService.getFeaturedCategories();
        return ResponseEntity.ok(categories);
    }
}