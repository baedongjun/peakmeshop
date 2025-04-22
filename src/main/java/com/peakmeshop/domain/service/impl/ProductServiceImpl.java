package com.peakmeshop.domain.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.peakmeshop.api.dto.*;
import com.peakmeshop.domain.entity.*;
import com.peakmeshop.domain.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.common.exception.BadRequestException;
import com.peakmeshop.domain.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductReviewRepository reviewRepository;
    private final ProductQnaRepository qnaRepository;
    private final ProductOptionRepository optionRepository;
    private final ProductOptionValueRepository optionValueRepository;
    private final ProductImageRepository productImageRepository;
    private final BrandRepository brandRepository;

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
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));
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
                    .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));
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
                .stock(productDTO.getStock())
                .status(productDTO.getStatus())
                .isActive(productDTO.getIsActive())
                .isFeatured(productDTO.getIsFeatured())
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
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

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
                    .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));
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
        if (productDTO.getStock() != null) {
            product.setStock(productDTO.getStock());
        }
        if (productDTO.getStatus() != null) {
            product.setStatus(productDTO.getStatus());
        }
        if (productDTO.getIsActive() != null) {
            product.setIsActive(productDTO.getIsActive());
        }
        if (productDTO.getIsFeatured() != null) {
            product.setIsFeatured(productDTO.getIsFeatured());
        }

        product.setUpdatedAt(LocalDateTime.now());
        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));
        productRepository.delete(product);
    }

    @Override
    @Transactional
    public void deleteProductImage(Long id) {
        ProductImage productImage = productImageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("상품 이미지를 찾을 수 없습니다."));
        productImageRepository.delete(productImage);
    }

    @Override
    @Transactional
    public ProductDTO toggleProductStatus(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        product.setIsActive(!product.getIsActive());
        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getProductsByCategory(Long categoryId, Integer minPrice, Integer maxPrice, List<Long> brandIds, Pageable pageable) {
        Page<Product> products;
        
        if (brandIds != null && !brandIds.isEmpty()) {
            if (minPrice != null && maxPrice != null) {
                products = productRepository.findByCategoryIdAndBrandIdInAndPriceBetween(
                    categoryId, brandIds, minPrice, maxPrice, pageable);
            } else {
                products = productRepository.findByCategoryIdAndBrandIdIn(categoryId, brandIds, pageable);
            }
        } else {
            if (minPrice != null && maxPrice != null) {
                products = productRepository.findByCategoryIdAndPriceBetween(
                    categoryId, minPrice, maxPrice, pageable);
            } else {
                products = productRepository.findByCategoryId(categoryId, pageable);
            }
        }
        
        return products.map(this::convertToDTO);
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
    public Page<ProductDTO> searchProducts(String query, Long categoryId, Integer minPrice, Integer maxPrice, List<Long> brandIds, Pageable pageable) {
        Page<Product> products;
        
        if (categoryId != null) {
            if (brandIds != null && !brandIds.isEmpty()) {
                if (minPrice != null && maxPrice != null) {
                    products = productRepository.findByNameContainingAndCategoryIdAndBrandIdInAndPriceBetween(
                        query, categoryId, brandIds, minPrice, maxPrice, pageable);
                } else {
                    products = productRepository.findByNameContainingAndCategoryIdAndBrandIdIn(
                        query, categoryId, brandIds, pageable);
                }
            } else {
                if (minPrice != null && maxPrice != null) {
                    products = productRepository.findByNameContainingAndCategoryIdAndPriceBetween(
                        query, categoryId, minPrice, maxPrice, pageable);
                } else {
                    products = productRepository.findByNameContainingAndCategoryId(query, categoryId, pageable);
                }
            }
        } else {
            if (brandIds != null && !brandIds.isEmpty()) {
                if (minPrice != null && maxPrice != null) {
                    products = productRepository.findByNameContainingAndBrandIdInAndPriceBetween(
                        query, brandIds, minPrice, maxPrice, pageable);
                } else {
                    products = productRepository.findByNameContainingAndBrandIdIn(query, brandIds, pageable);
                }
            } else {
                if (minPrice != null && maxPrice != null) {
                    products = productRepository.findByNameContainingAndPriceBetween(
                        query, minPrice, maxPrice, pageable);
                } else {
                    products = productRepository.findByNameContaining(query, pageable);
                }
            }
        }
        
        return products.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getFeaturedProducts(Pageable pageable) {
        Page<Product> products = productRepository.findByIsFeaturedTrueAndIsActiveTrue(pageable);
        return products.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getNewArrivals(Long categoryId, List<Long> brandIds, Pageable pageable) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Page<Product> products;
        
        if (categoryId != null) {
            if (brandIds != null && !brandIds.isEmpty()) {
                products = productRepository.findByCategoryIdAndBrandIdInAndCreatedAtAfter(
                    categoryId, brandIds, thirtyDaysAgo, pageable);
            } else {
                products = productRepository.findByCategoryIdAndCreatedAtAfter(
                    categoryId, thirtyDaysAgo, pageable);
            }
        } else {
            if (brandIds != null && !brandIds.isEmpty()) {
                products = productRepository.findByBrandIdInAndCreatedAtAfter(brandIds, thirtyDaysAgo, pageable);
            } else {
                products = productRepository.findByCreatedAtAfter(thirtyDaysAgo, pageable);
            }
        }
        
        return products.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getBestSellers(Long categoryId, List<Long> brandIds, String period, Pageable pageable) {
        LocalDateTime startDate;
        LocalDateTime endDate = LocalDateTime.now();
        
        // 기간 설정
        switch (period != null ? period : "all") {
            case "daily":
                startDate = endDate.minusDays(1);
                break;
            case "weekly":
                startDate = endDate.minusWeeks(1);
                break;
            case "monthly":
                startDate = endDate.minusMonths(1);
                break;
            default:
                startDate = endDate.minusYears(100); // 전체 기간
        }
        
        Page<Product> products;
        
        if (categoryId != null) {
            if (brandIds != null && !brandIds.isEmpty()) {
                products = productRepository.findBestSellersByCategoryAndBrandsAndPeriod(
                    categoryId, brandIds, startDate, endDate, pageable);
            } else {
                products = productRepository.findBestSellersByCategoryAndPeriod(
                    categoryId, startDate, endDate, pageable);
            }
        } else {
            if (brandIds != null && !brandIds.isEmpty()) {
                products = productRepository.findBestSellersByBrandsAndPeriod(
                    brandIds, startDate, endDate, pageable);
            } else {
                products = productRepository.findBestSellersByPeriod(startDate, endDate, pageable);
            }
        }
        
        return products.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getDiscountedProducts(Pageable pageable) {
        Page<Product> products = productRepository.findByIsActiveTrueAndSalePriceIsNotNullOrderBySalePriceAsc(pageable);
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
        List<Product> bestSellers = productRepository.findTop10ByIsActiveTrueOrderBySalesCountDesc();
        for (Product product : bestSellers) {
            product.setIsFeatured(true);
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
                .cost(product.getCost())
                .salePrice(product.getSalePrice())
                .discountedPrice(product.getSalePrice()) // salePrice를 discountedPrice로도 설정
                .brand(product.getBrand())
                .categoryId(product.getCategory().getId())
                .supplier(product.getSupplier())
                .mainImage(product.getMainImage())
                .stock(product.getStock())
                .stockAlert(product.getStockAlert())
                .maxPurchaseQuantity(product.getMaxPurchaseQuantity())
                .shortDescription(product.getShortDescription())
                .status(product.getStatus())
                .isActive(product.getIsActive())
                .isFeatured(product.getIsFeatured())
                .averageRating(product.getAverageRating())
                .reviewCount(product.getReviewCount())
                .salesCount(product.getSalesCount())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();

        return dto;
    }

    @Override
    @Transactional
    public ProductDTO updateProductStock(Long id, int stock) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

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
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        product.setStatus(status);
        product.setUpdatedAt(LocalDateTime.now());

        // 상태가 ACTIVE가 아니면 active를 false로 설정
        if (!"ACTIVE".equals(status)) {
            product.setIsActive(false);
        } else {
            product.setIsActive(true);
        }

        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    @Override
    public Map<String, Object> getProductSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalProducts", productRepository.count());
        summary.put("activeProducts", productRepository.countByIsActiveTrue());
        summary.put("inactiveProducts", productRepository.countByIsActiveFalse());
        summary.put("outOfStockProducts", productRepository.countByStockLessThanEqual(0));
        return summary;
    }

    @Override
    public Map<String, Object> getProductSummary(Long productId) {
        Map<String, Object> summary = new HashMap<>();
        productRepository.findById(productId).ifPresent(product -> {
            summary.put("totalSales", product.getTotalSales());
            summary.put("totalRevenue", product.getTotalRevenue());
            summary.put("totalReviews", reviewRepository.countByProductId(productId));
            summary.put("totalQnas", qnaRepository.countByProductId(productId));
        });
        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getProductStatistics(String startDate, String endDate, String interval, String category, Pageable pageable) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalProducts", productRepository.count());
        statistics.put("activeProducts", productRepository.countByIsActiveTrue());
        statistics.put("featuredProducts", productRepository.countByIsFeaturedTrue());
        statistics.put("totalSales", productRepository.calculateTotalSales().longValue());
        statistics.put("averageRating", productRepository.calculateAverageRating().doubleValue());

        // 카테고리별 판매 통계
        List<Object[]> salesByCategory = productRepository.findSalesByCategory(start, end);
        List<Map<String, Object>> categorySales = new ArrayList<>();
        for (Object[] row : salesByCategory) {
            Map<String, Object> map = new HashMap<>();
            map.put("category", row[0]);
            map.put("count", ((Number) row[1]).longValue());
            map.put("total", ((BigDecimal) row[2]).doubleValue());
            categorySales.add(map);
        }
        statistics.put("salesByCategory", categorySales);

        // 인기 상품
        Page<Product> topProducts = productRepository.findTopProducts(start, end, pageable);
        statistics.put("topProducts", topProducts.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));

        // 판매 추이
        List<Object[]> salesTrend = productRepository.findSalesTrend(start, end);
        List<Map<String, Object>> trend = new ArrayList<>();
        for (Object[] row : salesTrend) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", row[0]);
            map.put("count", ((Number) row[1]).longValue());
            map.put("total", ((BigDecimal) row[2]).doubleValue());
            trend.add(map);
        }
        statistics.put("salesTrend", trend);

        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getInventoryStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 카테고리별 재고 현황
        List<Object[]> stockByCategory = productRepository.findStockByCategory();
        List<Map<String, Object>> categoryStock = new ArrayList<>();
        for (Object[] row : stockByCategory) {
            Map<String, Object> map = new HashMap<>();
            map.put("category", row[0]);
            map.put("totalStock", ((Number) row[1]).longValue());
            categoryStock.add(map);
        }
        statistics.put("stockByCategory", categoryStock);

        // 재고 부족 상품
        List<Product> lowStockProducts = productRepository.findLowStockProducts();
        statistics.put("lowStockProducts", lowStockProducts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));

        // 품절 상품
        List<Product> outOfStockProducts = productRepository.findOutOfStockProducts();
        statistics.put("outOfStockProducts", outOfStockProducts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));

        return statistics;
    }

    @Override
    public Page<ProductReviewDTO> getProductReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable).map(this::convertToReviewDTO);
    }

    @Override
    public ProductReviewDTO getReviewById(Long id) {
        return reviewRepository.findById(id)
                .map(this::convertToReviewDTO)
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    @Override
    public Page<ProductQnaDTO> getProductQnas(Pageable pageable) {
        return qnaRepository.findAll(pageable).map(this::convertToQnaDTO);
    }

    @Override
    public ProductQnaDTO getQnaById(Long id) {
        return qnaRepository.findById(id)
                .map(this::convertToQnaDTO)
                .orElseThrow(() -> new RuntimeException("QnA not found"));
    }

    @Override
    public Map<String, Object> getInventory(String category) {
        Map<String, Object> inventory = new HashMap<>();
        if (category != null) {
            inventory.put("stockByCategory", productRepository.findStockByCategory());
        }
        inventory.put("lowStockProducts", productRepository.findLowStockProducts());
        inventory.put("outOfStockProducts", productRepository.findOutOfStockProducts());
        return inventory;
    }

    @Override
    public List<ProductOptionDTO> getProductOptions(Long productId) {
        return optionRepository.findByProductId(productId).stream()
                .map(this::convertToOptionDTO)
                .toList();
    }

    private ProductReviewDTO convertToReviewDTO(ProductReview review) {
        // ProductReview 엔티티를 DTO로 변환하는 로직
        return null; // TODO: 구현 필요
    }

    private ProductQnaDTO convertToQnaDTO(ProductQna qna) {
        // ProductQna 엔티티를 DTO로 변환하는 로직
        return null; // TODO: 구현 필요
    }

    private ProductOptionDTO convertToOptionDTO(ProductOption option) {
        // ProductOption 엔티티를 DTO로 변환하는 로직
        return null; // TODO: 구현 필요
    }

    private ProductOptionValueDTO convertToOptionValueDTO(ProductOptionValue value) {
        ProductOptionValueDTO dto = new ProductOptionValueDTO();
        dto.setId(value.getId());
        dto.setOptionId(value.getOption().getId());
        dto.setValue(value.getValue());
        dto.setAdditionalPrice(value.getAdditionalPrice());
        dto.setStock(value.getStock());
        dto.setSku(value.getSku());
        dto.setActive(value.isActive());
        dto.setCreatedAt(value.getCreatedAt());
        dto.setUpdatedAt(value.getUpdatedAt());
        return dto;
    }

    @Override
    public ProductSummaryDTO getProductSummary(String period, String startDate, String endDate) {
        LocalDateTime start;
        LocalDateTime end;

        // 기간 설정
        if (startDate != null && endDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            start = LocalDate.parse(startDate, formatter).atStartOfDay();
            end = LocalDate.parse(endDate, formatter).plusDays(1).atStartOfDay();
        } else {
            LocalDate now = LocalDate.now();
            if (period != null) {
                switch (period) {
                    case "daily":
                        start = now.atStartOfDay();
                        end = now.plusDays(1).atStartOfDay();
                        break;
                    case "weekly":
                        start = now.minusWeeks(1).atStartOfDay();
                        end = now.plusDays(1).atStartOfDay();
                        break;
                    case "monthly":
                        start = now.minusMonths(1).atStartOfDay();
                        end = now.plusDays(1).atStartOfDay();
                        break;
                    case "yearly":
                        start = now.minusYears(1).atStartOfDay();
                        end = now.plusDays(1).atStartOfDay();
                        break;
                    default:
                        start = now.minusMonths(1).atStartOfDay();
                        end = now.plusDays(1).atStartOfDay();
                }
            } else {
                // period가 null인 경우에 대한 기본 케이스 처리
                start = now.minusMonths(1).atStartOfDay();
                end = now.plusDays(1).atStartOfDay();
            }
        }

        // 상품 데이터 조회
        List<Product> products = productRepository.findByCreatedAtBetween(start, end);
        long totalProducts = productRepository.count();
        long activeProducts = productRepository.countByStatus("ACTIVE");
        long outOfStockProducts = productRepository.countByStockLessThanEqual(0);
        long lowStockProducts = productRepository.countByStockLessThanAndStockGreaterThan(10, 0);

        // 카테고리/브랜드 통계
        long totalCategories = categoryRepository.count();
        long totalBrands = brandRepository.count();

        // 가격/재고 통계
        double averagePrice = productRepository.calculateAveragePrice();
        long totalInventory = productRepository.calculateTotalInventory();
        double monthlyInventoryTurnover = productRepository.calculateInventoryTurnover(
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now());

        // 신규/인기 상품
        long monthlyNewProducts = productRepository.countByCreatedAtBetween(
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now());
        long monthlyTopSellers = productRepository.countTopSellers(
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now());

        return ProductSummaryDTO.builder()
                .totalProducts(totalProducts)
                .activeProducts(activeProducts)
                .outOfStockProducts(outOfStockProducts)
                .lowStockProducts(lowStockProducts)
                .totalCategories(totalCategories)
                .totalBrands(totalBrands)
                .averagePrice(averagePrice)
                .totalInventory(totalInventory)
                .monthlyNewProducts(monthlyNewProducts)
                .monthlyTopSellers(monthlyTopSellers)
                .monthlyInventoryTurnover(monthlyInventoryTurnover)
                .build();
    }

    @Override
    public List<Product> getProductsByIds(List<Long> ids) {
        return productRepository.findAllById(ids);
    }

    // 관련 상품 조회
    @Override
    public List<ProductDTO> getRelatedProducts(Long productId, int limit) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));
        
        // 같은 카테고리의 상품 중에서 연관 상품을 찾음
        Page<Product> relatedProducts = productRepository.findByCategoryIdAndIdNot(
            product.getCategory().getId(),
            productId,
            PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "viewCount"))
        );
        
        return relatedProducts.getContent().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    // 기본 카테고리별 상품 조회
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable)
                .map(this::convertToDTO);
    }

    // 기본 상품 검색
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByNameContainingOrDescriptionContaining(keyword, keyword, pageable)
                .map(this::convertToDTO);
    }

    // 기본 신상품 조회
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getNewArrivals(Pageable pageable) {
        return productRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable)
                .map(this::convertToDTO);
    }

    // 기본 베스트셀러 조회
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getBestSellers(Pageable pageable) {
        return productRepository.findByIsActiveTrueOrderBySalesCountDesc(pageable)
                .map(this::convertToDTO);
    }

    // 브랜드별 상품 조회 (정렬 조건 포함)
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getProductsByBrand(Long brandId, String sortType, Pageable pageable) {
        Sort sort;
        switch (sortType) {
            case "popularity":
                sort = Sort.by(Sort.Direction.DESC, "viewCount");
                break;
            case "new":
                sort = Sort.by(Sort.Direction.DESC, "createdAt");
                break;
            default:
                sort = pageable.getSort();
        }
        
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<Product> products = productRepository.findByBrandId(brandId, pageRequest);
        return products.map(this::convertToDTO);
    }

    // 브랜드별 상품 조회 (필터링 포함)
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getProductsByBrand(Long brandId, Long categoryId, Integer minPrice, Integer maxPrice, Pageable pageable) {
        Page<Product> products;
        
        if (categoryId != null) {
            if (minPrice != null && maxPrice != null) {
                products = productRepository.findByBrandIdAndCategoryIdAndPriceBetween(
                    brandId, categoryId, minPrice, maxPrice, pageable);
            } else {
                products = productRepository.findByBrandIdAndCategoryId(brandId, categoryId, pageable);
            }
        } else {
            if (minPrice != null && maxPrice != null) {
                products = productRepository.findByBrandIdAndPriceBetween(
                    brandId, minPrice, maxPrice, pageable);
            } else {
                products = productRepository.findByBrandId(brandId, pageable);
            }
        }
        
        return products.map(this::convertToDTO);
    }

    @Override
    @Transactional
    public void incrementViewCount(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));
        product.setViewCount(product.getViewCount() + 1);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getPopularProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdOrderByViewCountDesc(categoryId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getNewProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getRecommendedProducts(String username, int limit) {
        // 1. 사용자의 최근 구매 이력에서 카테고리와 브랜드 정보 추출
        // 2. 해당 카테고리/브랜드의 인기 상품 추천
        // 3. 사용자의 구매 이력이 없는 경우 전체 인기 상품 추천
        return productRepository.findTop10ByIsActiveTrueOrderBySalesCountDesc()
                .stream()
                .map(this::convertToDTO)
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductDTO> getRelatedProducts(Long productId, Pageable pageable) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        // 같은 카테고리의 상품들을 가져옴
        Page<Product> relatedProducts = productRepository.findByCategoryAndIdNot(
                product.getCategory(), productId, pageable);

        return relatedProducts.map(this::convertToDTO);
    }

    @Override
    public Page<ProductDTO> getFrequentlyBoughtTogether(Long productId, Pageable pageable) {
        // 주문 내역에서 함께 구매된 상품들을 조회
        Page<Product> frequentlyBoughtProducts = productRepository.findFrequentlyBoughtTogether(
                productId, pageable);

        return frequentlyBoughtProducts.map(this::convertToDTO);
    }

    @Override
    public Page<ProductDTO> getProducts(
            Long categoryId, Long brandId, Integer minPrice, Integer maxPrice, Boolean inStock,
            Pageable pageable) {
        // 조건에 맞는 상품들을 조회
        Page<Product> products = productRepository.findByFilters(
                categoryId, brandId, minPrice, maxPrice, inStock, pageable);

        return products.map(this::convertToDTO);
    }

    @Override
    public Page<ProductDTO> getProductReviews(Long productId, Pageable pageable) {
        // 상품의 리뷰들을 조회
        return reviewRepository.findByProductId(productId, pageable)
                .map(review -> {
                    ProductDTO productDTO = new ProductDTO();
                    productDTO.setId(review.getProduct().getId());
                    productDTO.setName(review.getProduct().getName());
                    // 필요한 다른 정보들도 설정
                    return productDTO;
                });
    }
}