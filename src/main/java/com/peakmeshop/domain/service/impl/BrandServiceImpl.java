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
import com.peakmeshop.api.mapper.BrandMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    @Transactional
    public BrandDTO createBrand(BrandDTO brandDTO) {
        Brand brand = brandMapper.toEntity(brandDTO);
        brand.setCreatedAt(LocalDateTime.now());
        brand.setUpdatedAt(LocalDateTime.now());
        Brand savedBrand = brandRepository.save(brand);
        return brandMapper.toDTO(savedBrand);
    }

    @Override
    @Transactional
    public BrandDTO updateBrand(Long id, BrandDTO brandDTO) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Brand not found with id: " + id));

        Brand updatedBrand = brandMapper.toEntity(brandDTO);
        brand.setName(updatedBrand.getName());
        brand.setSlug(updatedBrand.getSlug());
        brand.setDescription(updatedBrand.getDescription());
        brand.setLogoUrl(updatedBrand.getLogoUrl());
        brand.setWebsite(updatedBrand.getWebsite());
        brand.setIsActive(updatedBrand.getIsActive());
        brand.setIsFeatured(updatedBrand.getIsFeatured());
        brand.setUpdatedAt(LocalDateTime.now());

        Brand savedBrand = brandRepository.save(brand);
        return brandMapper.toDTO(savedBrand);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandDTO getBrandById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Brand not found with id: " + id));
        return brandMapper.toDTO(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BrandDTO> getBrandBySlug(String slug) {
        return brandRepository.findBySlug(slug).map(brandMapper::toDTO);
    }

    @Override
    public Page<BrandDTO> getAllBrandsPaged(Pageable pageable) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BrandDTO> getAllBrands(Pageable pageable) {
        Page<Brand> brandsPage = brandRepository.findAll(pageable);
        return brandsPage.map(brandMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandDTO> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(brandMapper::toDTO)
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
                .map(brandMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandDTO> getFeaturedBrands() {
        return brandRepository.findByIsFeaturedTrueAndIsActiveTrueOrderByNameAsc().stream()
                .map(brandMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BrandDTO> getActiveBrandsPaged(Pageable pageable) {
        Page<Brand> brandsPage = brandRepository.findByIsActiveTrue(pageable);
        return brandsPage.map(brandMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BrandDTO> getFeaturedBrandsPaged(Pageable pageable) {
        Page<Brand> brandsPage = brandRepository.findByIsFeaturedTrue(pageable);
        return brandsPage.map(brandMapper::toDTO);
    }

    @Override
    @Transactional
    public BrandDTO updateBrandStatus(Long id, boolean active) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Brand not found with id: " + id));

        brand.setIsActive(active);
        brand.setUpdatedAt(LocalDateTime.now());

        Brand updatedBrand = brandRepository.save(brand);
        return brandMapper.toDTO(updatedBrand);
    }

    @Override
    @Transactional
    public BrandDTO updateBrandFeatured(Long id, boolean featured) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Brand not found with id: " + id));

        brand.setIsFeatured(featured);
        brand.setUpdatedAt(LocalDateTime.now());

        Brand updatedBrand = brandRepository.save(brand);
        return brandMapper.toDTO(updatedBrand);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BrandDTO> searchBrands(String keyword, Pageable pageable) {
        Page<Brand> brandsPage = brandRepository.searchBrands(keyword, pageable);
        return brandsPage.map(brandMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandDTO> getTopBrandsByProductCount(int limit) {
        Pageable pageable = Pageable.ofSize(limit);
        return brandRepository.findTopBrandsByProductCount(pageable).stream()
                .map(brandMapper::toDTO)
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
        
        LocalDateTime start = null;
        LocalDateTime end = LocalDateTime.now();
        
        if (period != null) {
            switch (period) {
                case "daily":
                    start = end.minusDays(30);
                    statistics.put("dailyNewBrands", brandRepository.getDailyNewBrands(start, end));
                    statistics.put("dailySales", brandRepository.getDailySales(start, end));
                    break;
                case "weekly":
                    start = end.minusWeeks(12);
                    statistics.put("weeklyNewBrands", brandRepository.getWeeklyNewBrands(start, end));
                    statistics.put("weeklySales", brandRepository.getWeeklySales(start, end));
                    break;
                case "monthly":
                    start = end.minusMonths(12);
                    statistics.put("monthlyNewBrands", brandRepository.getMonthlyNewBrands(start, end));
                    statistics.put("monthlySales", brandRepository.getMonthlySales(start, end));
                    break;
                case "yearly":
                    start = end.minusYears(5);
                    statistics.put("yearlyNewBrands", brandRepository.getYearlyNewBrands(start, end));
                    statistics.put("yearlySales", brandRepository.getYearlySales(start, end));
                    break;
            }
        } else if (startDate != null && endDate != null) {
            start = LocalDate.parse(startDate).atStartOfDay();
            end = LocalDate.parse(endDate).atTime(23, 59, 59);
            statistics.put("customPeriodNewBrands", brandRepository.getCustomPeriodNewBrands(start, end));
            statistics.put("customPeriodSales", brandRepository.getCustomPeriodSales(start, end));
        }

        if (start != null) {
            statistics.put("topBrands", brandRepository.getTopBrandsByRevenue(start, end, 10));
            statistics.put("brandCategories", brandRepository.getBrandCategoriesDistribution(start, end));
            statistics.put("brandGrowth", calculateBrandGrowth(start, end));
        }

        return statistics;
    }

    private double calculateBrandGrowth(LocalDateTime start, LocalDateTime end) {
        long previousPeriodBrands = brandRepository.countBrandsBeforePeriod(start);
        long currentPeriodBrands = brandRepository.countBrandsInPeriod(start, end);
        
        if (previousPeriodBrands == 0) {
            return currentPeriodBrands > 0 ? 100.0 : 0.0;
        }
        
        return ((double) (currentPeriodBrands - previousPeriodBrands) / previousPeriodBrands) * 100;
    }
}

