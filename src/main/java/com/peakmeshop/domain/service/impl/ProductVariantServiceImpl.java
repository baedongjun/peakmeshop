package com.peakmeshop.domain.service.impl;

import com.peakmeshop.api.dto.ProductVariantDTO;
import com.peakmeshop.domain.entity.Product;
import com.peakmeshop.domain.entity.ProductAttribute;
import com.peakmeshop.domain.entity.ProductVariant;
import com.peakmeshop.domain.repository.ProductAttributeRepository;
import com.peakmeshop.domain.repository.ProductRepository;
import com.peakmeshop.domain.repository.ProductVariantRepository;
import com.peakmeshop.domain.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final ProductAttributeRepository productAttributeRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductVariantDTO> getVariantsByProductId(Long productId, Pageable pageable) {
        return productVariantRepository.findByProductId(productId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductVariantDTO> getVariantById(Long id) {
        return productVariantRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductVariantDTO> getVariantBySku(String sku) {
        return productVariantRepository.findBySku(sku)
                .map(this::convertToDTO);
    }

    @Override
    public ProductVariantDTO createVariant(ProductVariantDTO variantDTO) {
        // 상품 존재 여부 확인
        Product product = productRepository.findById(variantDTO.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + variantDTO.getProductId()));

        // SKU 중복 검사
        if (productVariantRepository.findBySku(variantDTO.getSku()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 SKU입니다: " + variantDTO.getSku());
        }

        ProductVariant variant = convertToEntity(variantDTO);
        variant.setProduct(product);

        ProductVariant savedVariant = productVariantRepository.save(variant);
        log.info("상품 옵션 생성: 상품={}, SKU={}", product.getName(), savedVariant.getSku());
        return convertToDTO(savedVariant);
    }

    @Override
    public ProductVariantDTO updateVariant(Long id, ProductVariantDTO variantDTO) {
        ProductVariant existingVariant = productVariantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품 옵션을 찾을 수 없습니다: " + id));

        // SKU가 변경되었고, 새 SKU가 이미 존재하는 경우 검사
        if (!existingVariant.getSku().equals(variantDTO.getSku()) &&
                productVariantRepository.findBySku(variantDTO.getSku()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 SKU입니다: " + variantDTO.getSku());
        }

        // 상품 ID가 변경된 경우 새 상품 존재 여부 확인
        if (!existingVariant.getProduct().getId().equals(variantDTO.getProductId())) {
            Product newProduct = productRepository.findById(variantDTO.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + variantDTO.getProductId()));
            existingVariant.setProduct(newProduct);
        }

        existingVariant.setSku(variantDTO.getSku());
        existingVariant.setName(variantDTO.getName());
        existingVariant.setPrice(variantDTO.getPrice());
        existingVariant.setQuantity(variantDTO.getQuantity());
        existingVariant.setAttributes(variantDTO.getAttributes());
        existingVariant.setEnabled(variantDTO.isEnabled());

        ProductVariant updatedVariant = productVariantRepository.save(existingVariant);
        log.info("상품 옵션 업데이트: SKU={}", updatedVariant.getSku());
        return convertToDTO(updatedVariant);
    }

    @Override
    public boolean deleteVariant(Long id) {
        return productVariantRepository.findById(id).map(variant -> {
            productVariantRepository.delete(variant);
            log.info("상품 옵션 삭제: SKU={}", variant.getSku());
            return true;
        }).orElse(false);
    }

    @Override
    public List<ProductVariantDTO> createVariantsFromCombinations(
            Long productId, Map<String, List<String>> attributeCombinations) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));

        // 모든 가능한 조합 생성
        List<Map<String, String>> combinations = generateCombinations(attributeCombinations);

        List<ProductVariantDTO> createdVariants = new ArrayList<>();

        // 기존 변형 가져오기
        List<ProductVariant> existingVariants = productVariantRepository.findByProductId(productId);

        for (Map<String, String> combination : combinations) {
            // 이미 동일한 속성 조합을 가진 옵션이 있는지 확인 (Java 코드로 필터링)
            boolean exists = existingVariants.stream()
                    .anyMatch(variant -> variant.getAttributes().equals(combination));

            if (exists) {
                log.info("이미 존재하는 속성 조합입니다: {}", combination);
                continue;
            }

            // 새 옵션 생성
            ProductVariant variant = new ProductVariant();
            variant.setProduct(product);

            // SKU 생성 (상품 코드 + 속성 값 조합)
            String sku = generateSku(product, combination);

            // SKU 중복 검사
            if (productVariantRepository.findBySku(sku).isPresent()) {
                // 중복된 경우 타임스탬프 추가
                sku = sku + "-" + System.currentTimeMillis();
            }

            variant.setSku(sku);

            // 이름 생성 (상품 이름 + 속성 값 조합)
            String name = generateName(product, combination);
            variant.setName(name);

            // 기본 가격은 상품 가격으로 설정
            variant.setPrice(product.getPrice());

            // 기본 수량은 0으로 설정
            variant.setQuantity(0);

            // 속성 설정
            variant.setAttributes(combination);

            // 기본적으로 활성화
            variant.setEnabled(true);

            ProductVariant savedVariant = productVariantRepository.save(variant);
            log.info("상품 옵션 생성 (조합): 상품={}, SKU={}", product.getName(), savedVariant.getSku());

            createdVariants.add(convertToDTO(savedVariant));
        }

        return createdVariants;
    }

    @Override
    public void updateVariantQuantity(Long id, int quantity) {
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품 옵션을 찾을 수 없습니다: " + id));

        variant.setQuantity(quantity);
        productVariantRepository.save(variant);
        log.info("상품 옵션 수량 업데이트: SKU={}, 수량={}", variant.getSku(), quantity);
    }

    @Override
    public void updateVariantPrice(Long id, BigDecimal price) {
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품 옵션을 찾을 수 없습니다: " + id));

        variant.setPrice(price);
        productVariantRepository.save(variant);
        log.info("상품 옵션 가격 업데이트: SKU={}, 가격={}", variant.getSku(), price);
    }

    @Override
    public void enableVariant(Long id) {
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품 옵션을 찾을 수 없습니다: " + id));

        variant.setEnabled(true);
        productVariantRepository.save(variant);
        log.info("상품 옵션 활성화: SKU={}", variant.getSku());
    }

    @Override
    public void disableVariant(Long id) {
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품 옵션을 찾을 수 없습니다: " + id));

        variant.setEnabled(false);
        productVariantRepository.save(variant);
        log.info("상품 옵션 비활성화: SKU={}", variant.getSku());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, String>> getPossibleCombinations(Long productId) {
        // 상품에 연결된 속성 조회 (메서드 이름 수정)
        List<ProductAttribute> attributes = productAttributeRepository.findByProduct_Id(productId);

        // 속성별 옵션 값 맵 생성
        Map<String, List<String>> attributeOptions = new HashMap<>();
        for (ProductAttribute attribute : attributes) {
            attributeOptions.put(attribute.getCode(), attribute.getOptions());
        }

        // 모든 가능한 조합 생성
        return generateCombinations(attributeOptions);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVariantInStock(Long id) {
        return productVariantRepository.findById(id)
                .map(variant -> variant.getQuantity() > 0)
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public int getVariantQuantity(Long id) {
        return productVariantRepository.findById(id)
                .map(ProductVariant::getQuantity)
                .orElse(0);
    }

    // 헬퍼 메서드
    private ProductVariantDTO convertToDTO(ProductVariant variant) {
        return ProductVariantDTO.builder()
                .id(variant.getId())
                .productId(variant.getProduct().getId())
                .sku(variant.getSku())
                .name(variant.getName())
                .price(variant.getPrice())
                .quantity(variant.getQuantity())
                .attributes(variant.getAttributes())
                .enabled(variant.isEnabled())
                .createdAt(variant.getCreatedAt())
                .updatedAt(variant.getUpdatedAt())
                .build();
    }

    private ProductVariant convertToEntity(ProductVariantDTO dto) {
        ProductVariant variant = new ProductVariant();
        variant.setSku(dto.getSku());
        variant.setName(dto.getName());
        variant.setPrice(dto.getPrice());
        variant.setQuantity(dto.getQuantity());
        variant.setAttributes(dto.getAttributes());
        variant.setEnabled(dto.isEnabled());
        return variant;
    }

    private List<Map<String, String>> generateCombinations(Map<String, List<String>> attributeOptions) {
        List<Map<String, String>> result = new ArrayList<>();
        generateCombinationsRecursive(attributeOptions, new ArrayList<>(attributeOptions.keySet()), 0, new HashMap<>(), result);
        return result;
    }

    private void generateCombinationsRecursive(
            Map<String, List<String>> attributeOptions,
            List<String> attributeNames,
            int index,
            Map<String, String> current,
            List<Map<String, String>> result) {

        if (index == attributeNames.size()) {
            result.add(new HashMap<>(current));
            return;
        }

        String attributeName = attributeNames.get(index);
        List<String> options = attributeOptions.get(attributeName);

        for (String option : options) {
            current.put(attributeName, option);
            generateCombinationsRecursive(attributeOptions, attributeNames, index + 1, current, result);
        }
    }

    private String generateSku(Product product, Map<String, String> attributes) {
        StringBuilder sku = new StringBuilder(product.getCode() != null ? product.getCode() : "P" + product.getId());

        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            sku.append("-").append(entry.getValue().replaceAll("\\s+", ""));
        }

        return sku.toString();
    }

    private String generateName(Product product, Map<String, String> attributes) {
        StringBuilder name = new StringBuilder(product.getName());

        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            name.append(" - ").append(entry.getValue());
        }

        return name.toString();
    }
}