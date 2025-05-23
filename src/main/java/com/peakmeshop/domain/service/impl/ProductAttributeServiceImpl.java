package com.peakmeshop.domain.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.peakmeshop.domain.entity.ProductAttributeOption;
import com.peakmeshop.domain.entity.ProductOption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.ProductAttributeDTO;
import com.peakmeshop.domain.entity.ProductAttribute;
import com.peakmeshop.domain.repository.ProductAttributeRepository;
import com.peakmeshop.domain.repository.ProductAttributeOptionRepository;
import com.peakmeshop.domain.service.ProductAttributeService;
import com.peakmeshop.api.mapper.ProductAttributeMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductAttributeServiceImpl implements ProductAttributeService {

    private final ProductAttributeRepository productAttributeRepository;
    private final ProductAttributeOptionRepository productAttributeOptionRepository;
    private final ProductAttributeMapper productAttributeMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductAttributeDTO> getAllAttributes(Pageable pageable) {
        return productAttributeRepository.findAll(pageable)
                .map(productAttributeMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductAttributeDTO> getAttributeById(Long id) {
        return productAttributeRepository.findById(id)
                .map(productAttributeMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductAttributeDTO> getAttributeByCode(String code) {
        return productAttributeRepository.findByCode(code)
                .map(productAttributeMapper::toDTO);
    }

    @Override
    public ProductAttributeDTO createAttribute(ProductAttributeDTO attributeDTO) {
        // 코드 중복 검사
        if (productAttributeRepository.findByCode(attributeDTO.getCode()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 속성 코드입니다: " + attributeDTO.getCode());
        }

        ProductAttribute attribute = productAttributeMapper.toEntity(attributeDTO);
        ProductAttribute savedAttribute = productAttributeRepository.save(attribute);
        log.info("상품 속성 생성: {}", savedAttribute.getProduct().getName());
        return productAttributeMapper.toDTO(savedAttribute);
    }

    @Override
    public ProductAttributeDTO updateAttribute(Long id, ProductAttributeDTO attributeDTO) {
        ProductAttribute existingAttribute = productAttributeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품 속성을 찾을 수 없습니다: " + id));

        // 코드가 변경되었고, 새 코드가 이미 존재하는 경우 검사
        if (!existingAttribute.getProduct().getCode().equals(attributeDTO.getCode()) &&
                productAttributeRepository.findByCode(attributeDTO.getCode()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 속성 코드입니다: " + attributeDTO.getCode());
        }

        ProductAttribute updatedAttribute = productAttributeMapper.toEntity(attributeDTO);
        updatedAttribute.setId(existingAttribute.getId());
        updatedAttribute.setCreatedAt(existingAttribute.getCreatedAt());
        updatedAttribute.setProduct(existingAttribute.getProduct());

        ProductAttribute savedAttribute = productAttributeRepository.save(updatedAttribute);
        log.info("상품 속성 업데이트: {}", savedAttribute.getProduct().getName());
        return productAttributeMapper.toDTO(savedAttribute);
    }

    @Override
    public boolean deleteAttribute(Long id) {
        return productAttributeRepository.findById(id).map(attribute -> {
            productAttributeRepository.delete(attribute);
            log.info("상품 속성 삭제: {}", attribute.getProduct().getName());
            return true;
        }).orElse(false);
    }


    @Override
    @Transactional
    public void addAttributeOption(Long attributeId, ProductOption option) {
        ProductAttribute attribute = productAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new IllegalArgumentException("상품 속성을 찾을 수 없습니다: " + attributeId));
        
        ProductAttributeOption attributeOption = new ProductAttributeOption();
        attributeOption.setName(option.getName());
        attributeOption.setDisplayName(option.getDisplayName());
        attributeOption.setType(option.getType());
        attributeOption.setRequired(option.isRequired());
        attributeOption.setFilterable(option.isFilterable());
        attributeOption.setSearchable(option.isSearchable());
        attributeOption.setComparable(option.isComparable());
        attributeOption.setShowInProductListing(option.isShowInProductListing());
        attributeOption.setSortOrder(option.getSortOrder());
        attributeOption.setEnabled(true);
        
        attribute.setAttributeOption(attributeOption);
        productAttributeRepository.save(attribute);
    }

    @Override
    @Transactional
    public void removeAttributeOption(Long attributeId, ProductOption option) {
        ProductAttribute attribute = productAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new IllegalArgumentException("상품 속성을 찾을 수 없습니다: " + attributeId));
        
        attribute.setAttributeOption(null);
        productAttributeRepository.save(attribute);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAttributeOptions(Long productId, String type) {
        return productAttributeRepository.findByProductId(productId).stream()
                .filter(attr -> attr.getAttributeOption().getType().equals(type))
                .map(ProductAttribute::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAttributeOptionValues(Long attributeOptionId) {
        ProductAttributeOption option = productAttributeOptionRepository.findById(attributeOptionId)
                .orElseThrow(() -> new IllegalArgumentException("Attribute option not found"));
        return productAttributeRepository.findByAttributeOptionId(attributeOptionId).stream()
                .map(ProductAttribute::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public ProductAttributeOption getAttributeOption(Long attributeOptionId) {
        return productAttributeOptionRepository.findById(attributeOptionId)
                .orElseThrow(() -> new IllegalArgumentException("Attribute option not found"));
    }

    @Override
    public String getCode(Long attributeOptionId) {
        ProductAttributeOption option = productAttributeOptionRepository.findById(attributeOptionId)
                .orElseThrow(() -> new IllegalArgumentException("Attribute option not found"));
        return option.getName();
    }
}