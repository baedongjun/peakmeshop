package com.peakmeshop.domain.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.ProductBundleDTO;
import com.peakmeshop.api.mapper.ProductBundleMapper;
import com.peakmeshop.domain.entity.Product;
import com.peakmeshop.domain.entity.ProductBundle;
import com.peakmeshop.domain.entity.ProductBundleItem;
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
    private final ProductBundleMapper productBundleMapper;

    @Override
    @Transactional(readOnly = true)
    public ProductBundleDTO getBundleById(Long id) {
        ProductBundle bundle = bundleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product bundle not found with id: " + id));
        return productBundleMapper.toDTO(bundle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductBundleDTO> getAllBundles() {
        List<ProductBundle> bundles = bundleRepository.findAll();
        return bundles.stream()
                .map(productBundleMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductBundleDTO> getAllBundles(Pageable pageable) {
        Page<ProductBundle> bundles = bundleRepository.findAll(pageable);
        return bundles.map(productBundleMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductBundleDTO> getActiveBundles(Pageable pageable) {
        Page<ProductBundle> bundles = bundleRepository.findByIsActiveTrue(pageable);
        return bundles.map(productBundleMapper::toDTO);
    }

    @Override
    @Transactional
    public ProductBundleDTO createBundle(ProductBundleDTO bundleDTO) {
        ProductBundle bundle = productBundleMapper.toEntity(bundleDTO);
        bundle.setCreatedAt(LocalDateTime.now());
        ProductBundle savedBundle = bundleRepository.save(bundle);

        // 번들 아이템 저장
        if (bundleDTO.getItems() != null) {
            for (ProductBundleItem items : bundleDTO.getItems()) {
                Product product = productRepository.findById(items.getProduct().getId())
                        .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + items.getProduct().getId()));

                ProductBundleItem item = ProductBundleItem.builder()
                        .bundle(savedBundle)
                        .product(product)
                        .quantity(items.getQuantity())
                        .build();

                bundleItemRepository.save(item);
            }
        }

        return getBundleById(savedBundle.getId());
    }

    @Override
    @Transactional
    public ProductBundleDTO updateBundle(Long id, ProductBundleDTO bundleDTO) {
        if (!bundleRepository.existsById(id)) {
            throw new EntityNotFoundException("Product bundle not found with id: " + id);
        }

        ProductBundle updatedBundle = productBundleMapper.toEntity(bundleDTO);
        updatedBundle.setId(id);
        updatedBundle.setUpdatedAt(LocalDateTime.now());
        bundleRepository.save(updatedBundle);

        // 기존 번들 아이템 삭제
        bundleItemRepository.deleteByBundleId(id);

        // 새 번들 아이템 저장
        if (bundleDTO.getItems() != null) {
            for (ProductBundleItem items : bundleDTO.getItems()) {
                Product product = productRepository.findById(items.getProduct().getId())
                        .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + items.getProduct().getId()));

                ProductBundleItem item = ProductBundleItem.builder()
                        .bundle(updatedBundle)
                        .product(product)
                        .quantity(items.getQuantity())
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
            throw new EntityNotFoundException("Product bundle not found with id: " + id);
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
                .orElseThrow(() -> new EntityNotFoundException("Product bundle not found with id: " + id));

        bundle.setActive(true);
        bundle.setUpdatedAt(LocalDateTime.now());

        bundleRepository.save(bundle);
    }

    @Override
    @Transactional
    public void deactivateBundle(Long id) {
        ProductBundle bundle = bundleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product bundle not found with id: " + id));

        bundle.setActive(false);
        bundle.setUpdatedAt(LocalDateTime.now());

        bundleRepository.save(bundle);
    }
}