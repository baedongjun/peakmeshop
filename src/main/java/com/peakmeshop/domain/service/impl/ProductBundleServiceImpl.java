package com.peakmeshop.domain.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.ProductBundleDTO;
import com.peakmeshop.api.dto.ProductBundleItemDTO;
import com.peakmeshop.domain.entity.Product;
import com.peakmeshop.domain.entity.ProductBundle;
import com.peakmeshop.domain.entity.ProductBundleItem;
import com.peakmeshop.common.exception.ResourceNotFoundException;
import com.peakmeshop.domain.repository.ProductBundleItemRepository;
import com.peakmeshop.domain.repository.ProductBundleRepository;
import com.peakmeshop.domain.repository.ProductRepository;
import com.peakmeshop.domain.service.ProductBundleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductBundleServiceImpl implements ProductBundleService {

    private final ProductBundleRepository bundleRepository;
    private final ProductBundleItemRepository bundleItemRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public ProductBundleDTO getBundleById(Long id) {
        ProductBundle bundle = bundleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product bundle not found with id: " + id));
        return convertToDTO(bundle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductBundleDTO> getAllBundles() {
        List<ProductBundle> bundles = bundleRepository.findAll();
        return bundles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductBundleDTO> getAllBundles(Pageable pageable) {
        Page<ProductBundle> bundles = bundleRepository.findAll(pageable);
        return bundles.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductBundleDTO> getActiveBundles(Pageable pageable) {
        Page<ProductBundle> bundles = bundleRepository.findByIsActiveTrue(pageable);
        return bundles.map(this::convertToDTO);
    }

    @Override
    @Transactional
    public ProductBundleDTO createBundle(ProductBundleDTO bundleDTO) {
        ProductBundle bundle = ProductBundle.builder()
                .name(bundleDTO.getName())
                .description(bundleDTO.getDescription())
                .discountRate(bundleDTO.getDiscountRate())
                .discountAmount(bundleDTO.getDiscountAmount())
                .isActive(bundleDTO.isActive())
                .startDate(bundleDTO.getStartDate())
                .endDate(bundleDTO.getEndDate())
                .createdAt(LocalDateTime.now())
                .build();

        ProductBundle savedBundle = bundleRepository.save(bundle);

        // 번들 아이템 저장
        if (bundleDTO.getItems() != null) {
            for (ProductBundleItemDTO itemDTO : bundleDTO.getItems()) {
                Product product = productRepository.findById(itemDTO.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDTO.getProductId()));

                ProductBundleItem item = ProductBundleItem.builder()
                        .bundle(savedBundle)
                        .product(product)
                        .quantity(itemDTO.getQuantity())
                        .build();

                bundleItemRepository.save(item);
            }
        }

        return getBundleById(savedBundle.getId());
    }

    @Override
    @Transactional
    public ProductBundleDTO updateBundle(Long id, ProductBundleDTO bundleDTO) {
        ProductBundle bundle = bundleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product bundle not found with id: " + id));

        bundle.setName(bundleDTO.getName());
        bundle.setDescription(bundleDTO.getDescription());
        bundle.setDiscountRate(bundleDTO.getDiscountRate());
        bundle.setDiscountAmount(bundleDTO.getDiscountAmount());
        bundle.setActive(bundleDTO.isActive());
        bundle.setStartDate(bundleDTO.getStartDate());
        bundle.setEndDate(bundleDTO.getEndDate());
        bundle.setUpdatedAt(LocalDateTime.now());

        bundleRepository.save(bundle);

        // 기존 번들 아이템 삭제
        bundleItemRepository.deleteByBundleId(id);

        // 새 번들 아이템 저장
        if (bundleDTO.getItems() != null) {
            for (ProductBundleItemDTO itemDTO : bundleDTO.getItems()) {
                Product product = productRepository.findById(itemDTO.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDTO.getProductId()));

                ProductBundleItem item = ProductBundleItem.builder()
                        .bundle(bundle)
                        .product(product)
                        .quantity(itemDTO.getQuantity())
                        .build();

                bundleItemRepository.save(item);
            }
        }

        return getBundleById(id);
    }

    @Override
    @Transactional
    public void deleteBundle(Long id) {
        if (!bundleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product bundle not found with id: " + id);
        }

        // 번들 아이템 삭제
        bundleItemRepository.deleteByBundleId(id);

        // 번들 삭제
        bundleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void activateBundle(Long id) {
        ProductBundle bundle = bundleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product bundle not found with id: " + id));

        bundle.setActive(true);
        bundle.setUpdatedAt(LocalDateTime.now());

        bundleRepository.save(bundle);
    }

    @Override
    @Transactional
    public void deactivateBundle(Long id) {
        ProductBundle bundle = bundleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product bundle not found with id: " + id));

        bundle.setActive(false);
        bundle.setUpdatedAt(LocalDateTime.now());

        bundleRepository.save(bundle);
    }

    private ProductBundleDTO convertToDTO(ProductBundle bundle) {
        List<ProductBundleItemDTO> itemDTOs = bundle.getItems().stream()
                .map(this::convertToItemDTO)
                .collect(Collectors.toList());

        return ProductBundleDTO.builder()
                .id(bundle.getId())
                .name(bundle.getName())
                .description(bundle.getDescription())
                .discountRate(bundle.getDiscountRate())
                .discountAmount(bundle.getDiscountAmount())
                .active(bundle.isActive())
                .startDate(bundle.getStartDate())
                .endDate(bundle.getEndDate())
                .createdAt(bundle.getCreatedAt())
                .updatedAt(bundle.getUpdatedAt())
                .items(itemDTOs)
                .build();
    }

    private ProductBundleItemDTO convertToItemDTO(ProductBundleItem item) {
        Product product = item.getProduct();

        return ProductBundleItemDTO.builder()
                .id(item.getId())
                .bundleId(item.getBundle().getId())
                .productId(product.getId())
                .productName(product.getName())
                .productImage(product.getMainImage())
                .price(product.getPrice())
                .quantity(item.getQuantity())
                .build();
    }
}