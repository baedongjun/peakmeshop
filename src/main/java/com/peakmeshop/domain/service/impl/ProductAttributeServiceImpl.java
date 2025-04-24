package com.peakmeshop.domain.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.ProductAttributeDTO;
import com.peakmeshop.domain.entity.ProductAttribute;
import com.peakmeshop.domain.repository.ProductAttributeRepository;
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
        log.info("상품 속성 생성: {}", savedAttribute.getName());
        return productAttributeMapper.toDTO(savedAttribute);
    }

    @Override
    public ProductAttributeDTO updateAttribute(Long id, ProductAttributeDTO attributeDTO) {
        ProductAttribute existingAttribute = productAttributeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품 속성을 찾을 수 없습니다: " + id));

        // 코드가 변경되었고, 새 코드가 이미 존재하는 경우 검사
        if (!existingAttribute.getCode().equals(attributeDTO.getCode()) &&
                productAttributeRepository.findByCode(attributeDTO.getCode()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 속성 코드입니다: " + attributeDTO.getCode());
        }

        ProductAttribute updatedAttribute = productAttributeMapper.toEntity(attributeDTO);
        updatedAttribute.setId(existingAttribute.getId());
        updatedAttribute.setCreatedAt(existingAttribute.getCreatedAt());
        updatedAttribute.setProduct(existingAttribute.getProduct());

        ProductAttribute savedAttribute = productAttributeRepository.save(updatedAttribute);
        log.info("상품 속성 업데이트: {}", savedAttribute.getName());
        return productAttributeMapper.toDTO(savedAttribute);
    }

    @Override
    public boolean deleteAttribute(Long id) {
        return productAttributeRepository.findById(id).map(attribute -> {
            productAttributeRepository.delete(attribute);
            log.info("상품 속성 삭제: {}", attribute.getName());
            return true;
        }).orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductAttributeDTO> getFilterableAttributes() {
        return productAttributeRepository.findByFilterableTrue().stream()
                .map(productAttributeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductAttributeDTO> getSearchableAttributes() {
        return productAttributeRepository.findBySearchableTrue().stream()
                .map(productAttributeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductAttributeDTO> getComparableAttributes() {
        return productAttributeRepository.findByComparableTrue().stream()
                .map(productAttributeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductAttributeDTO> getAttributesForProductListing() {
        return productAttributeRepository.findByShowInProductListingTrue().stream()
                .map(productAttributeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void addAttributeOption(Long attributeId, String option) {
        ProductAttribute attribute = productAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new IllegalArgumentException("상품 속성을 찾을 수 없습니다: " + attributeId));

        if (!attribute.getOptions().contains(option)) {
            attribute.getOptions().add(option);
            productAttributeRepository.save(attribute);
            log.info("속성 옵션 추가: 속성={}, 옵션={}", attribute.getName(), option);
        }
    }

    @Override
    public void removeAttributeOption(Long attributeId, String option) {
        ProductAttribute attribute = productAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new IllegalArgumentException("상품 속성을 찾을 수 없습니다: " + attributeId));

        if (attribute.getOptions().remove(option)) {
            productAttributeRepository.save(attribute);
            log.info("속성 옵션 제거: 속성={}, 옵션={}", attribute.getName(), option);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAttributeOptions(Long attributeId) {
        return productAttributeRepository.findById(attributeId)
                .map(ProductAttribute::getOptions)
                .orElseThrow(() -> new IllegalArgumentException("상품 속성을 찾을 수 없습니다: " + attributeId));
    }
}