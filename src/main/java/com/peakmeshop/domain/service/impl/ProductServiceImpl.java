package com.peakmeshop.domain.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.ProductDTO;
import com.peakmeshop.domain.entity.Category;
import com.peakmeshop.domain.entity.Product;
import com.peakmeshop.common.exception.BadRequestException;
import com.peakmeshop.common.exception.ResourceNotFoundException;
import com.peakmeshop.domain.repository.CategoryRepository;
import com.peakmeshop.domain.repository.ProductRepository;
import com.peakmeshop.domain.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다."));
        return convertToDTO(product);
    }

    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        // 상품 코드 중복 확인
        if (productDTO.getCode() != null && !productDTO.getCode().isEmpty()) {
            if (productRepository.findByCode(productDTO.getCode()).isPresent()) {
                throw new BadRequestException("이미 존재하는 상품 코드입니다: " + productDTO.getCode());
            }
        }

        // 카테고리 확인
        Category category = null;
        if (productDTO.getCategoryId() != null) {
            category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("카테고리를 찾을 수 없습니다."));
        }

        Product product = Product.builder()
                .code(productDTO.getCode())
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .salePrice(productDTO.getSalePrice())
                .brand(productDTO.getBrand())
                .category(category)
                .mainImage(productDTO.getMainImage())
                .images(productDTO.getImages())
                .stock(productDTO.getStock())
                .status(productDTO.getStatus())
                .active(productDTO.isActive())
                .featured(productDTO.isFeatured())
                .averageRating(0.0)
                .reviewCount(0)
                .salesCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다."));

        // 상품 코드 중복 확인 (변경된 경우)
        if (productDTO.getCode() != null && !productDTO.getCode().equals(product.getCode())) {
            if (productRepository.findByCode(productDTO.getCode()).isPresent()) {
                throw new BadRequestException("이미 존재하는 상품 코드입니다: " + productDTO.getCode());
            }
            product.setCode(productDTO.getCode());
        }

        // 카테고리 확인 (변경된 경우)
        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("카테고리를 찾을 수 없습니다."));
            product.setCategory(category);
        }

        if (productDTO.getName() != null) {
            product.setName(productDTO.getName());
        }
        if (productDTO.getDescription() != null) {
            product.setDescription(productDTO.getDescription());
        }
        if (productDTO.getPrice() != null) {
            product.setPrice(productDTO.getPrice());
        }
        if (productDTO.getSalePrice() != null) {
            product.setSalePrice(productDTO.getSalePrice());
        }
        if (productDTO.getBrand() != null) {
            product.setBrand(productDTO.getBrand());
        }
        if (productDTO.getMainImage() != null) {
            product.setMainImage(productDTO.getMainImage());
        }
        if (productDTO.getImages() != null) {
            product.setImages(productDTO.getImages());
        }
        if (productDTO.getStock() != null) {
            product.setStock(productDTO.getStock());
        }
        if (productDTO.getStatus() != null) {
            product.setStatus(productDTO.getStatus());
        }
        if (productDTO.getActive() != null) {
            product.setActive(productDTO.getActive());
        }
        if (productDTO.getFeatured() != null) {
            product.setFeatured(productDTO.getFeatured());
        }

        product.setUpdatedAt(LocalDateTime.now());
        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다."));
        productRepository.delete(product);
    }

    @Override
    @Transactional
    public ProductDTO toggleProductStatus(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다."));

        product.setActive(!product.getActive());
        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getProductsByCategory(Long categoryId, Pageable pageable) {
        Page<Product> productsPage = productRepository.findByCategoryId(categoryId, pageable);
        List<ProductDTO> productDTOs = productsPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(productDTOs, pageable, productsPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getProductsByBrand(String brand, Pageable pageable) {
        Page<Product> productsPage = productRepository.findByBrand(brand, pageable);
        List<ProductDTO> productDTOs = productsPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(productDTOs, pageable, productsPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProducts(String keyword, Pageable pageable) {
        Page<Product> productsPage = productRepository.findByNameContainingOrDescriptionContaining(keyword, keyword, pageable);
        List<ProductDTO> productDTOs = productsPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(productDTOs, pageable, productsPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getFeaturedProducts(Pageable pageable) {
        Page<Product> products = productRepository.findByFeaturedTrueAndActiveTrue(pageable);
        return products.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getNewArrivals(Pageable pageable) {
        Page<Product> products = productRepository.findByActiveTrueOrderByCreatedAtDesc(pageable);
        return products.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getBestSellers(Pageable pageable) {
        Page<Product> products = productRepository.findByActiveTrueOrderBySalesCountDesc(pageable);
        return products.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getDiscountedProducts(Pageable pageable) {
        Page<Product> products = productRepository.findByActiveTrueAndSalePriceIsNotNullOrderBySalePriceAsc(pageable);
        return products.map(this::convertToDTO);
    }

    @Override
    @Transactional
    public void updateProductRankings() {
        // 판매량, 리뷰 수, 평점 등을 기반으로 상품 랭킹 업데이트
        // 예: 베스트셀러, 인기 상품 등의 랭킹 계산
        log.info("상품 랭킹 업데이트 실행");

        // 여기에 랭킹 업데이트 로직 구현
        // 예: 판매량이 많은 상품을 베스트셀러로 표시
        List<Product> bestSellers = productRepository.findTop10ByActiveTrueOrderBySalesCountDesc();
        for (Product product : bestSellers) {
            product.setFeatured(true);
            productRepository.save(product);
        }
    }

    // Entity를 DTO로 변환하는 메서드
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = ProductDTO.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .salePrice(product.getSalePrice())
                .discountedPrice(product.getSalePrice()) // salePrice를 discountedPrice로도 설정
                .brand(product.getBrand())
                .mainImage(product.getMainImage())
                .images(product.getImages())
                .stock(product.getStock())
                .status(product.getStatus())
                .active(product.getActive())
                .featured(product.getFeatured())
                .averageRating(product.getAverageRating())
                .reviewCount(product.getReviewCount())
                .salesCount(product.getSalesCount())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();

        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }

        return dto;
    }

    @Override
    @Transactional
    public ProductDTO updateProductStock(Long id, int stock) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다."));

        product.setStock(stock);
        product.setUpdatedAt(LocalDateTime.now());

        // 재고가 0이면 상태를 OUT_OF_STOCK으로 변경
        if (stock <= 0) {
            product.setStatus("OUT_OF_STOCK");
        } else if ("OUT_OF_STOCK".equals(product.getStatus())) {
            product.setStatus("ACTIVE");
        }

        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    @Override
    @Transactional
    public ProductDTO updateProductStatus(Long id, String status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다."));

        product.setStatus(status);
        product.setUpdatedAt(LocalDateTime.now());

        // 상태가 ACTIVE가 아니면 active를 false로 설정
        if (!"ACTIVE".equals(status)) {
            product.setActive(false);
        } else {
            product.setActive(true);
        }

        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }
}