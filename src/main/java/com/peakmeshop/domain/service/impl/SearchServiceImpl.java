package com.peakmeshop.domain.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.ProductDTO;
import com.peakmeshop.api.dto.SearchDTO;
import com.peakmeshop.domain.entity.Product;
import com.peakmeshop.domain.repository.CategoryRepository;
import com.peakmeshop.domain.repository.ProductRepository;
import com.peakmeshop.domain.service.ProductService;
import com.peakmeshop.domain.service.SearchService;

@Service
public class SearchServiceImpl implements SearchService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductService productService;

    public SearchServiceImpl(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ProductService productService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productService = productService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> search(SearchDTO searchDTO, Pageable pageable) {
        // 검색 조건 구성
        String keyword = searchDTO.keyword();
        Long categoryId = searchDTO.categoryId();
        List<Long> brandIds = searchDTO.brandIds();
        Double minPrice = searchDTO.minPrice();
        Double maxPrice = searchDTO.maxPrice();
        Boolean inStock = searchDTO.inStock();
        Boolean onSale = searchDTO.onSale();

        // 상품 검색
        Page<Product> productPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            // 키워드 검색
            if (categoryId != null) {
                // 카테고리 내 키워드 검색
                productPage = productRepository.findByNameContainingOrDescriptionContainingAndCategoryId(
                        keyword, keyword, categoryId, pageable);
            } else {
                // 전체 키워드 검색
                productPage = productRepository.findByNameContainingOrDescriptionContaining(
                        keyword, keyword, pageable);
            }
        } else if (categoryId != null) {
            // 카테고리 검색
            productPage = productRepository.findByCategoryId(categoryId, pageable);
        } else {
            // 전체 상품 검색
            productPage = productRepository.findAll(pageable);
        }

        // 필터링
        List<Product> filteredProducts = productPage.getContent().stream()
                .filter(product -> {
                    // 브랜드 필터링
                    if (brandIds != null && !brandIds.isEmpty()) {
                        return product.getBrand() != null && brandIds.contains(product.getBrand().getId());
                    }
                    return true;
                })
                .filter(product -> {
                    // 가격 필터링
                    boolean priceMatch = true;
                    if (minPrice != null) {
                        priceMatch = priceMatch && product.getPrice().compareTo(BigDecimal.valueOf(minPrice)) >= 0;
                    }
                    if (maxPrice != null) {
                        priceMatch = priceMatch && product.getPrice().compareTo(BigDecimal.valueOf(maxPrice)) <= 0;
                    }
                    return priceMatch;
                })
                .filter(product -> {
                    // 재고 필터링
                    if (inStock != null && inStock) {
                        return product.getStock() > 0;
                    }
                    return true;
                })
                .filter(product -> {
                    // 할인 상품 필터링
                    if (onSale != null && onSale) {
                        return product.getSalePrice() != null;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        // 필터링된 결과로 새 Page 객체 생성
        Page<Product> filteredPage = new PageImpl<>(
                filteredProducts,
                pageable,
                filteredProducts.size()
        );

        // 결과를 DTO로 변환
        List<ProductDTO> productDTOs = filteredPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(productDTOs, pageable, filteredPage.getTotalElements());
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
    public Page<ProductDTO> searchByAttribute(String attributeName, String attributeValue, Pageable pageable) {
        // 속성 기반 검색 구현
        // 실제 구현에서는 상품 속성 테이블을 조회
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
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
        // 전체 상품 인덱싱 로직 구현
        // 검색 엔진 사용 시 필요
    }

    @Override
    @Transactional
    public void indexProduct(Long productId) {
        // 개별 상품 인덱싱 로직 구현
        // 검색 엔진 사용 시 필요
    }

    @Override
    @Transactional
    public void removeProductFromIndex(Long productId) {
        // 상품 인덱스 제거 로직 구현
        // 검색 엔진 사용 시 필요
    }

    // 엔티티를 DTO로 변환
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setCode(product.getCode());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setSalePrice(product.getSalePrice());
        dto.setBrand(product.getBrand());
        dto.setCategory(product.getCategory());
        dto.setSupplier(product.getSupplier());
        dto.setMainImage(product.getMainImage());
        dto.setStock(product.getStock());
        dto.setStatus(product.getStatus());
        dto.setIsActive(product.getIsActive());
        dto.setIsFeatured(product.getIsFeatured());
        dto.setAverageRating(product.getAverageRating());
        dto.setReviewCount(product.getReviewCount());
        dto.setSalesCount(product.getSalesCount());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }
}