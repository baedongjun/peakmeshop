package com.peakmeshop.api.controller;

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

import com.peakmeshop.api.dto.BrandDTO;
import com.peakmeshop.domain.service.BrandService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    public ResponseEntity<BrandDTO> createBrand(@RequestBody BrandDTO brandDTO) {
        BrandDTO createdBrand = brandService.createBrand(brandDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBrand);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandDTO> getBrandById(@PathVariable Long id) {
        BrandDTO brand = brandService.getBrandById(id);
        return ResponseEntity.ok(brand);
    }

    @GetMapping
    public ResponseEntity<Page<BrandDTO>> getAllBrands(Pageable pageable) {
        Page<BrandDTO> brands = brandService.getAllBrandsPaged(pageable);
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BrandDTO>> getAllBrandsWithoutPaging() {
        List<BrandDTO> brands = brandService.getAllBrands();
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<BrandDTO> getBrandBySlug(@PathVariable String slug) {
        return brandService.getBrandBySlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    public ResponseEntity<List<BrandDTO>> getActiveBrands() {
        List<BrandDTO> brands = brandService.getActiveBrands();
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<BrandDTO>> getFeaturedBrands() {
        List<BrandDTO> brands = brandService.getFeaturedBrands();
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/active/paged")
    public ResponseEntity<Page<BrandDTO>> getActiveBrandsPaged(Pageable pageable) {
        Page<BrandDTO> brands = brandService.getActiveBrandsPaged(pageable);
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/featured/paged")
    public ResponseEntity<Page<BrandDTO>> getFeaturedBrandsPaged(Pageable pageable) {
        Page<BrandDTO> brands = brandService.getFeaturedBrandsPaged(pageable);
        return ResponseEntity.ok(brands);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandDTO> updateBrand(@PathVariable Long id, @RequestBody BrandDTO brandDTO) {
        BrandDTO updatedBrand = brandService.updateBrand(id, brandDTO);
        return ResponseEntity.ok(updatedBrand);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteBrand(@PathVariable Long id) {
        boolean deleted = brandService.deleteBrand(id);
        return ResponseEntity.ok(Map.of("deleted", deleted));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<BrandDTO> updateBrandStatus(
            @PathVariable Long id, @RequestParam boolean active) {
        BrandDTO updatedBrand = brandService.updateBrandStatus(id, active);
        return ResponseEntity.ok(updatedBrand);
    }

    @PutMapping("/{id}/featured")
    public ResponseEntity<BrandDTO> updateBrandFeatured(
            @PathVariable Long id, @RequestParam boolean featured) {
        BrandDTO updatedBrand = brandService.updateBrandFeatured(id, featured);
        return ResponseEntity.ok(updatedBrand);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BrandDTO>> searchBrands(
            @RequestParam String keyword, Pageable pageable) {
        Page<BrandDTO> brands = brandService.searchBrands(keyword, pageable);
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/top")
    public ResponseEntity<List<BrandDTO>> getTopBrandsByProductCount(
            @RequestParam(defaultValue = "10") int limit) {
        List<BrandDTO> brands = brandService.getTopBrandsByProductCount(limit);
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/{id}/product-count")
    public ResponseEntity<Integer> getProductCountByBrandId(@PathVariable Long id) {
        int count = brandService.getProductCountByBrandId(id);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/check-slug")
    public ResponseEntity<Map<String, Boolean>> checkSlugUnique(
            @RequestParam String slug,
            @RequestParam(required = false) Long brandId) {
        boolean isUnique = brandService.isSlugUnique(slug, brandId);
        return ResponseEntity.ok(Map.of("unique", isUnique));
    }

    @GetMapping("/check-name")
    public ResponseEntity<Map<String, Boolean>> checkNameUnique(
            @RequestParam String name,
            @RequestParam(required = false) Long brandId) {
        boolean isUnique = brandService.isNameUnique(name, brandId);
        return ResponseEntity.ok(Map.of("unique", isUnique));
    }
}

