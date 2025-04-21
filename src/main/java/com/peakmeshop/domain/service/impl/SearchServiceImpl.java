package com.peakmeshop.domain.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.BrandDTO;
import com.peakmeshop.api.dto.CategoryDTO;
import com.peakmeshop.api.dto.ProductDTO;
import com.peakmeshop.api.dto.SearchDTO;
import com.peakmeshop.domain.entity.Brand;
import com.peakmeshop.domain.entity.Category;
import com.peakmeshop.domain.entity.Product;
import com.peakmeshop.domain.repository.BrandRepository;
import com.peakmeshop.domain.repository.CategoryRepository;
import com.peakmeshop.domain.repository.ProductRepository;
import com.peakmeshop.domain.repository.SearchKeywordRepository;
import com.peakmeshop.domain.service.ProductService;
import com.peakmeshop.domain.service.SearchService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SearchKeywordRepository searchKeywordRepository;
    private final ProductService productService;
    private static final Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Override
    public Page<ProductDTO> searchProducts(
        String keyword,
        Long categoryId,
        List<Long> brandIds,
        Integer minPrice,
        Integer maxPrice,
        Pageable pageable
    ) {
        Page<Product> products;
        
        if (categoryId != null) {
            if (brandIds != null && !brandIds.isEmpty()) {
                if (minPrice != null && maxPrice != null) {
                    products = productRepository.findByNameContainingAndCategoryIdAndBrandIdInAndPriceBetween(
                        keyword, categoryId, brandIds, minPrice, maxPrice, pageable);
                } else {
                    products = productRepository.findByNameContainingAndCategoryIdAndBrandIdIn(
                        keyword, categoryId, brandIds, pageable);
                }
            } else {
                if (minPrice != null && maxPrice != null) {
                    products = productRepository.findByNameContainingAndCategoryIdAndPriceBetween(
                        keyword, categoryId, minPrice, maxPrice, pageable);
                } else {
                    products = productRepository.findByNameContainingAndCategoryId(keyword, categoryId, pageable);
                }
            }
        } else {
            if (brandIds != null && !brandIds.isEmpty()) {
                if (minPrice != null && maxPrice != null) {
                    products = productRepository.findByNameContainingAndBrandIdInAndPriceBetween(
                        keyword, brandIds, minPrice, maxPrice, pageable);
                } else {
                    products = productRepository.findByNameContainingAndBrandIdIn(keyword, brandIds, pageable);
                }
            } else {
                if (minPrice != null && maxPrice != null) {
                    products = productRepository.findByNameContainingAndPriceBetween(
                        keyword, minPrice, maxPrice, pageable);
                } else {
                    products = productRepository.findByNameContaining(keyword, pageable);
                }
            }
        }
        
        return products.map(this::convertToDTO);
    }

    @Override
    public List<CategoryDTO> searchCategories(String keyword) {
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase(keyword);
        return categories.stream()
            .map(this::convertToCategoryDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<BrandDTO> searchBrands(String keyword) {
        List<Brand> brands = brandRepository.findByNameContainingIgnoreCase(keyword);
        return brands.stream()
            .map(this::convertToBrandDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<String> getRealtimePopularKeywords() {
        return searchKeywordRepository.findTop10ByOrderBySearchCountDesc();
    }

    @Override
    public List<String> getDailyPopularKeywords() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return searchKeywordRepository.findTop10ByCreatedAtBetweenOrderBySearchCountDesc(startOfDay, endOfDay);
    }

    @Override
    public List<String> getWeeklyPopularKeywords() {
        LocalDateTime startOfWeek = LocalDateTime.now().minusWeeks(1);
        LocalDateTime endOfWeek = LocalDateTime.now();
        return searchKeywordRepository.findTop10ByCreatedAtBetweenOrderBySearchCountDesc(startOfWeek, endOfWeek);
    }

    @Override
    public List<String> getSuggestKeywords(String keyword) {
        return searchKeywordRepository.findTop10ByKeywordStartingWith(keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> search(SearchDTO searchDTO, Pageable pageable) {
        String keyword = searchDTO.keyword();
        Long categoryId = searchDTO.categoryId();
        List<Long> brandIds = searchDTO.brandIds();
        Double minPrice = searchDTO.minPrice();
        Double maxPrice = searchDTO.maxPrice();
        Boolean inStock = searchDTO.inStock();
        Boolean onSale = searchDTO.onSale();

        // 검색 조건에 따라 적절한 메서드 호출
        if (keyword != null && !keyword.trim().isEmpty()) {
            return searchProducts(keyword, categoryId, brandIds, 
                minPrice != null ? minPrice.intValue() : null,
                maxPrice != null ? maxPrice.intValue() : null,
                pageable);
        } else if (categoryId != null) {
            return searchByCategory(categoryId, pageable);
        } else if (brandIds != null && !brandIds.isEmpty()) {
            return searchByBrand(brandIds.get(0), pageable);
        } else if (minPrice != null || maxPrice != null) {
            return searchByPriceRange(minPrice, maxPrice, pageable);
        } else if (inStock != null) {
            return searchInStock(inStock, pageable);
        } else if (onSale != null) {
            return searchOnSale(onSale, pageable);
        }

        // 기본 검색: 모든 상품 반환
        return productService.getAllProducts(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchByKeyword(String keyword, Pageable pageable) {
        Page<Product> productPage = productRepository.findByNameContainingOrDescriptionContaining(
                keyword, keyword, pageable);

        List<ProductDTO> productDTOs = productPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(productDTOs, pageable, productPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchByCategory(Long categoryId, Pageable pageable) {
        Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageable);

        List<ProductDTO> productDTOs = productPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(productDTOs, pageable, productPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchByBrand(Long brandId, Pageable pageable) {
        Page<Product> productPage = productRepository.findByBrandId(brandId, pageable);

        List<ProductDTO> productDTOs = productPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(productDTOs, pageable, productPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchByPriceRange(Double minPrice, Double maxPrice, Pageable pageable) {
        Page<Product> productPage = productRepository.findByPriceBetween(
                BigDecimal.valueOf(minPrice), BigDecimal.valueOf(maxPrice), pageable);

        List<ProductDTO> productDTOs = productPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(productDTOs, pageable, productPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchByAttribute(String name, String value, Pageable pageable) {
        // 상품 속성 검색은 별도의 인덱싱이나 검색 엔진이 필요할 수 있음
        // 현재는 간단한 구현으로 대체
        Page<Product> productPage = productRepository.findAll(pageable);
        List<Product> filteredProducts = productPage.getContent().stream()
                .filter(product -> hasAttribute(product, name, value))
                .collect(Collectors.toList());

        return new PageImpl<>(
                filteredProducts.stream().map(this::convertToDTO).collect(Collectors.toList()),
                pageable,
                filteredProducts.size()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchInStock(boolean inStock, Pageable pageable) {
        Page<Product> productPage;
        if (inStock) {
            productPage = productRepository.findByStockGreaterThan(0, pageable);
        } else {
            productPage = productRepository.findByStockEquals(0, pageable);
        }

        List<ProductDTO> productDTOs = productPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(productDTOs, pageable, productPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchOnSale(boolean onSale, Pageable pageable) {
        Page<Product> productPage;
        if (onSale) {
            productPage = productRepository.findBySalePriceIsNotNull(pageable);
        } else {
            productPage = productRepository.findBySalePriceIsNull(pageable);
        }

        List<ProductDTO> productDTOs = productPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(productDTOs, pageable, productPage.getTotalElements());
    }

    @Override
    @Transactional
    public void indexAllProducts() {
        // 검색 엔진 인덱싱 구현
        // 현재는 더미 구현
        log.info("모든 상품 인덱싱 시작");
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            indexProduct(product.getId());
        }
        log.info("모든 상품 인덱싱 완료");
    }

    @Override
    @Transactional
    public void indexProduct(Long productId) {
        // 검색 엔진 인덱싱 구현
        // 현재는 더미 구현
        log.info("상품 인덱싱: {}", productId);
        productRepository.findById(productId).ifPresent(product -> {
            // 검색 엔진에 상품 정보 인덱싱
        });
    }

    @Override
    @Transactional
    public void removeProductFromIndex(Long productId) {
        try {
            // 검색 엔진에서 상품 제거 로직 구현
            log.info("상품 ID {}를 검색 인덱스에서 제거했습니다.", productId);
        } catch (Exception e) {
            log.error("상품 ID {}를 검색 인덱스에서 제거하는 중 오류가 발생했습니다: {}", productId, e.getMessage());
            throw new RuntimeException("검색 인덱스에서 상품을 제거하는 중 오류가 발생했습니다.", e);
        }
    }

    private boolean hasAttribute(Product product, String name, String value) {
        // 상품 속성 검사 로직 구현
        // 현재는 더미 구현
        return false;
    }

    private ProductDTO convertToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .salePrice(product.getSalePrice())
                .mainImage(product.getMainImage())
                .brand(product.getBrand())
                .category(product.getCategory())
                .averageRating(product.getAverageRating())
                .reviewCount(product.getReviewCount())
                .build();
    }

    private CategoryDTO convertToCategoryDTO(Category category) {
        return CategoryDTO.builder()
            .id(category.getId())
            .name(category.getName())
            .description(category.getDescription())
            .build();
    }

    private BrandDTO convertToBrandDTO(Brand brand) {
        return BrandDTO.builder()
            .id(brand.getId())
            .name(brand.getName())
            .description(brand.getDescription())
            .build();
    }
}