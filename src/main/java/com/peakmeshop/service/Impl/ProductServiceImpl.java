package com.peakmeshop.service.Impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.dto.ProductDTO;
import com.peakmeshop.entity.Category;
import com.peakmeshop.entity.Product;
import com.peakmeshop.exception.ResourceNotFoundException;
import com.peakmeshop.repository.CategoryRepository;
import com.peakmeshop.repository.ProductRepository;
import com.peakmeshop.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // 생성자 주입 (Java 17에서는 @Autowired 생략 가능)
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> findAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList(); // Java 16+에서 추가된 toList() 메서드 사용
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> findProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDTO> findProductById(Long id) {
        return productRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> searchProductsByName(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional
    public ProductDTO saveProduct(ProductDTO productDTO) {
        // Java 17 패턴 매칭 사용
        Category category = categoryRepository.findById(productDTO.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + productDTO.categoryId()));

        // var 키워드 사용 (Java 10+)
        var product = convertToEntity(productDTO);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        product.setIsAvailable(true);

        var savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        // Java 17 패턴 매칭과 var 키워드 사용
        var existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // 카테고리 변경 시 존재 여부 확인
        if (!existingProduct.getCategory().getId().equals(productDTO.categoryId())) {
            categoryRepository.findById(productDTO.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found with id: " + productDTO.categoryId()));
        }

        // 기존 값 유지를 위한 처리
        var productToUpdate = convertToEntity(productDTO);
        productToUpdate.setId(id);
        productToUpdate.setCreatedAt(existingProduct.getCreatedAt());
        productToUpdate.setUpdatedAt(LocalDateTime.now());

        var updatedProduct = productRepository.save(productToUpdate);
        return convertToDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
    }

    @Override
    @Transactional
    public ProductDTO updateProductStock(Long id, Integer quantity) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        int newStock = product.getStockQuantity() + quantity;
        if (newStock < 0) {
            throw new IllegalArgumentException("Insufficient stock for product: " + id);
        }

        product.setStockQuantity(newStock);
        product.setUpdatedAt(LocalDateTime.now());

        var updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    // Entity -> DTO 변환 (Java 17 스타일)
    private ProductDTO convertToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .stockQuantity(product.getStockQuantity())
                .mainImageUrl(product.getMainImageUrl())
                .additionalImageUrls(product.getAdditionalImageUrls())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .isAvailable(product.getIsAvailable())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .brand(product.getBrand())
                .averageRating(product.getAverageRating())
                .reviewCount(product.getReviewCount())
                .build();
    }

    // DTO -> Entity 변환 (Java 17 스타일)
    private Product convertToEntity(ProductDTO dto) {
        var product = new Product();
        product.setId(dto.id());
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setDiscountPrice(dto.discountPrice());
        product.setStockQuantity(dto.stockQuantity());
        product.setMainImageUrl(dto.mainImageUrl());
        product.setAdditionalImageUrls(dto.additionalImageUrls());

        // 카테고리 설정 (Optional 활용)
        categoryRepository.findById(dto.categoryId())
                .ifPresent(product::setCategory);

        product.setIsAvailable(dto.isAvailable());
        product.setBrand(dto.brand());

        return product;
    }
}