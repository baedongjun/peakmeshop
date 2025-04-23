package com.peakmeshop.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.BrandDTO;

public interface BrandService {

    // 기본 CRUD 작업
    BrandDTO createBrand(BrandDTO brandDTO);

    BrandDTO updateBrand(Long id, BrandDTO brandDTO);

    BrandDTO getBrandById(Long id);

    Optional<BrandDTO> getBrandBySlug(String slug);

    Page<BrandDTO> getAllBrandsPaged(Pageable pageable);

    List<BrandDTO> getAllBrands();

    boolean deleteBrand(Long id);

    // 추가 기능
    List<BrandDTO> getActiveBrands();

    List<BrandDTO> getFeaturedBrands();

    Page<BrandDTO> getActiveBrandsPaged(Pageable pageable);

    Page<BrandDTO> getFeaturedBrandsPaged(Pageable pageable);

    BrandDTO updateBrandStatus(Long id, boolean active);

    BrandDTO updateBrandFeatured(Long id, boolean featured);

    Page<BrandDTO> searchBrands(String keyword, Pageable pageable);

    List<BrandDTO> getTopBrandsByProductCount(int limit);

    int getProductCountByBrandId(Long brandId);

    boolean isSlugUnique(String slug, Long brandId);

    boolean isNameUnique(String name, Long brandId);

    Page<BrandDTO> getAllBrands(Pageable pageable);

    Map<String, Long> getBrandSummary();

    Map<String, Long> getBrandSummary(Long brandId);

    Map<String, Object> getBrandStatistics(String period, String startDate, String endDate);
}

