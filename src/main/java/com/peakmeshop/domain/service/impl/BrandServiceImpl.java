package com.peakmeshop.domain.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.BrandDTO;
import com.peakmeshop.domain.entity.Brand;
import com.peakmeshop.domain.repository.BrandRepository;
import com.peakmeshop.domain.service.BrandService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Override
    @Transactional
    public BrandDTO createBrand(BrandDTO brandDTO) {
        Brand brand = mapToEntity(brandDTO);
        brand.setCreatedAt(LocalDateTime.now());
        brand.setUpdatedAt(LocalDateTime.now());
        Brand savedBrand = brandRepository.save(brand);
        return mapToDTO(savedBrand);
    }

    @Override
    @Transactional
    public BrandDTO updateBrand(Long id, BrandDTO brandDTO) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Brand not found with id: " + id));

        brand.setName(brandDTO.name());
        brand.setSlug(brandDTO.slug());
        brand.setDescription(brandDTO.description());
        brand.setLogoUrl(brandDTO.logoUrl());
        brand.setWebsite(brandDTO.website());
        brand.setIsActive(brandDTO.isActive());
        brand.setIsFeatured(brandDTO.isFeatured());
        brand.setUpdatedAt(LocalDateTime.now());

        Brand updatedBrand = brandRepository.save(brand);
        return mapToDTO(updatedBrand);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandDTO getBrandById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Brand not found with id: " + id));
        return mapToDTO(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BrandDTO> getBrandBySlug(String slug) {
        return brandRepository.findBySlug(slug).map(this::mapToDTO);
    }

    @Override
    public Page<BrandDTO> getAllBrandsPaged(Pageable pageable) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BrandDTO> getAllBrands(Pageable pageable) {
        Page<Brand> brandsPage = brandRepository.findAll(pageable);
        return brandsPage.map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandDTO> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean deleteBrand(Long id) {
        if (brandRepository.existsById(id)) {
            brandRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandDTO> getActiveBrands() {
        return brandRepository.findByIsActiveTrueOrderByNameAsc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandDTO> getFeaturedBrands() {
        return brandRepository.findByIsFeaturedTrueAndIsActiveTrueOrderByNameAsc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BrandDTO> getActiveBrandsPaged(Pageable pageable) {
        Page<Brand> brandsPage = brandRepository.findByIsActiveTrue(pageable);
        return brandsPage.map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BrandDTO> getFeaturedBrandsPaged(Pageable pageable) {
        Page<Brand> brandsPage = brandRepository.findByIsFeaturedTrue(pageable);
        return brandsPage.map(this::mapToDTO);
    }

    @Override
    @Transactional
    public BrandDTO updateBrandStatus(Long id, boolean active) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Brand not found with id: " + id));

        brand.setIsActive(active);
        brand.setUpdatedAt(LocalDateTime.now());

        Brand updatedBrand = brandRepository.save(brand);
        return mapToDTO(updatedBrand);
    }

    @Override
    @Transactional
    public BrandDTO updateBrandFeatured(Long id, boolean featured) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Brand not found with id: " + id));

        brand.setIsFeatured(featured);
        brand.setUpdatedAt(LocalDateTime.now());

        Brand updatedBrand = brandRepository.save(brand);
        return mapToDTO(updatedBrand);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BrandDTO> searchBrands(String keyword, Pageable pageable) {
        Page<Brand> brandsPage = brandRepository.searchBrands(keyword, pageable);
        return brandsPage.map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandDTO> getTopBrandsByProductCount(int limit) {
        Pageable pageable = Pageable.ofSize(limit);
        return brandRepository.findTopBrandsByProductCount(pageable).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public int getProductCountByBrandId(Long brandId) {
        return brandRepository.countProductsByBrandId(brandId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSlugUnique(String slug, Long brandId) {
        Optional<Brand> existingBrand = brandRepository.findBySlug(slug);
        return existingBrand.isEmpty() || existingBrand.get().getId().equals(brandId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isNameUnique(String name, Long brandId) {
        boolean exists = brandRepository.existsByName(name);
        if (!exists) {
            return true;
        }

        // 이름이 존재하지만 현재 브랜드의 이름인 경우
        if (brandId != null) {
            Brand brand = brandRepository.findById(brandId).orElse(null);
            return brand != null && brand.getName().equals(name);
        }

        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getBrandSummary(Long brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new UsernameNotFoundException("Brand not found with id: " + brandId));

        Map<String, Long> summary = new HashMap<>();
        summary.put("id", brand.getId());
        summary.put("productCount", (long) getProductCountByBrandId(brandId));
        summary.put("totalSales", brandRepository.calculateTotalSalesByBrandId(brandId).longValue());
        summary.put("totalOrders", brandRepository.countOrdersByBrandId(brandId));
        summary.put("averageRating", brandRepository.calculateAverageRatingByBrandId(brandId).longValue());

        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getBrandSummary() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth = now.withDayOfMonth(now.toLocalDate().lengthOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        Map<String, Long> summary = new HashMap<>();
        summary.put("totalBrands", brandRepository.count());
        summary.put("activeBrands", brandRepository.countByIsActiveTrue());
        summary.put("featuredBrands", brandRepository.countByIsFeaturedTrue());
        summary.put("totalProducts", brandRepository.countTotalProducts());
        summary.put("totalSales", brandRepository.calculateTotalSales().longValue());
        summary.put("averageRating", brandRepository.calculateAverageRating().longValue());
        summary.put("monthlyNewBrands", brandRepository.countMonthlyNewBrands(startOfMonth, endOfMonth));

        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getBrandStatistics(String period, String startDate, String endDate) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 기간별 통계 데이터 수집
        LocalDateTime start = null;
        LocalDateTime end = LocalDateTime.now();
        
        if (period != null) {
            switch (period) {
                case "daily":
                    start = end.minusDays(1);
                    break;
                case "weekly":
                    start = end.minusWeeks(1);
                    break;
                case "monthly":
                    start = end.minusMonths(1);
                    break;
                case "yearly":
                    start = end.minusYears(1);
                    break;
                default:
                    start = end.minusMonths(1); // 기본값: 1개월
            }
        } else if (startDate != null && endDate != null) {
            // 날짜 형식: yyyy-MM-dd
            start = LocalDate.parse(startDate).atStartOfDay();
            end = LocalDate.parse(endDate).atTime(23, 59, 59);
        } else {
            start = end.minusMonths(1); // 기본값: 1개월
        }

        // 브랜드별 통계 수집
        List<Brand> brands = brandRepository.findAll();
        final LocalDateTime finalStart = start;
        final LocalDateTime finalEnd = end;
        List<Map<String, Object>> brandStats = brands.stream()
            .map(brand -> {
                Map<String, Object> stat = new HashMap<>();
                stat.put("id", brand.getId());
                stat.put("name", brand.getName());
                stat.put("productCount", getProductCountByBrandId(brand.getId()));
                stat.put("orderCount", brandRepository.countOrdersByBrandIdAndDateRange(brand.getId(), finalStart, finalEnd));
                stat.put("totalSales", brandRepository.calculateTotalSalesByBrandIdAndDateRange(brand.getId(), finalStart, finalEnd));
                return stat;
            })
            .collect(Collectors.toList());

        statistics.put("brands", brandStats);
        statistics.put("totalBrands", brands.size());
        statistics.put("activeBrands", brandRepository.countByIsActiveTrue());
        statistics.put("featuredBrands", brandRepository.countByIsFeaturedTrue());
        statistics.put("period", period);
        statistics.put("startDate", start);
        statistics.put("endDate", end);

        return statistics;
    }

    // Entity -> DTO 변환
    private BrandDTO mapToDTO(Brand brand) {
        int productCount = brand.getProducts() != null ? brand.getProducts().size() : 0;

        return new BrandDTO(
                brand.getId(),
                brand.getName(),
                brand.getNameEn(),
                brand.getSlug(),
                brand.getDescription(),
                brand.getLogoUrl(),
                brand.getWebsite(),
                brand.getIsActive() != null ? brand.getIsActive() : false,
                brand.getIsFeatured() != null ? brand.getIsFeatured() : false,
                0, // displayOrder - 엔티티에 없는 필드
                "", // metaTitle - 엔티티에 없는 필드
                "", // metaDescription - 엔티티에 없는 필드
                "", // metaKeywords - 엔티티에 없는 필드
                productCount,
                brand.getCreatedAt(),
                brand.getUpdatedAt()
        );
    }

    // DTO -> Entity 변환
    private Brand mapToEntity(BrandDTO brandDTO) {
        Brand brand = new Brand();
        brand.setName(brandDTO.name());
        brand.setNameEn(brandDTO.nameEn());
        brand.setSlug(brandDTO.slug());
        brand.setDescription(brandDTO.description());
        brand.setLogoUrl(brandDTO.logoUrl());
        brand.setWebsite(brandDTO.website());
        brand.setIsActive(brandDTO.isActive());
        brand.setIsFeatured(brandDTO.isFeatured());
        return brand;
    }
}

