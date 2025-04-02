package com.peakmeshop.domain.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.BrandDTO;
import com.peakmeshop.domain.entity.Brand;
import com.peakmeshop.domain.repository.BrandRepository;
import com.peakmeshop.domain.service.BrandService;
import com.peakmeshop.common.exception.ResourceNotFoundException;

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
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));

        brand.setName(brandDTO.name());
        brand.setSlug(brandDTO.slug());
        brand.setDescription(brandDTO.description());
        brand.setLogoUrl(brandDTO.logo());
        brand.setWebsite(brandDTO.website());
        brand.setIsActive(brandDTO.isActive());
        brand.setIsFeatured(brandDTO.isFeatured());
        brand.setFeatured(brandDTO.isFeatured()); // 중복 필드 동기화
        brand.setUpdatedAt(LocalDateTime.now());

        Brand updatedBrand = brandRepository.save(brand);
        return mapToDTO(updatedBrand);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandDTO getBrandById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));
        return mapToDTO(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BrandDTO> getBrandBySlug(String slug) {
        return brandRepository.findBySlug(slug).map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BrandDTO> getAllBrandsPaged(Pageable pageable) {
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
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));

        brand.setIsActive(active);
        brand.setUpdatedAt(LocalDateTime.now());

        Brand updatedBrand = brandRepository.save(brand);
        return mapToDTO(updatedBrand);
    }

    @Override
    @Transactional
    public BrandDTO updateBrandFeatured(Long id, boolean featured) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));

        brand.setIsFeatured(featured);
        brand.setFeatured(featured); // 중복 필드 동기화
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

    // Entity -> DTO 변환
    private BrandDTO mapToDTO(Brand brand) {
        int productCount = brand.getProducts() != null ? brand.getProducts().size() : 0;

        return new BrandDTO(
                brand.getId(),
                brand.getName(),
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
        brand.setSlug(brandDTO.slug());
        brand.setDescription(brandDTO.description());
        brand.setLogoUrl(brandDTO.logo());
        brand.setWebsite(brandDTO.website());
        brand.setIsActive(brandDTO.isActive());
        brand.setIsFeatured(brandDTO.isFeatured());
        brand.setFeatured(brandDTO.isFeatured()); // 중복 필드 동기화
        return brand;
    }
}

