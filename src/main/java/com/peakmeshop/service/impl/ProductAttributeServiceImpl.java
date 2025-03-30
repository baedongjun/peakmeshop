package com.peakmeshop.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.dto.ProductAttributeDTO;
import com.peakmeshop.entity.ProductAttribute;
import com.peakmeshop.repository.ProductAttributeRepository;
import com.peakmeshop.service.ProductAttributeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductAttributeServiceImpl implements ProductAttributeService {

    private final ProductAttributeRepository productAttributeRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductAttributeDTO> getAllAttributes(Pageable pageable) {
        return productAttributeRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductAttributeDTO> getAttributeById(Long id) {
        return productAttributeRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductAttributeDTO> getAttributeByCode(String code) {
        return productAttributeRepository.findByCode(code)
                .map(this::convertToDTO);
    }

    @Override
    public ProductAttributeDTO createAttribute(ProductAttributeDTO attributeDTO) {
        // 코드 중복 검사
        if (productAttributeRepository.findByCode(attributeDTO.getCode()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 속성 코드입니다: " + attributeDTO.getCode());
        }

        ProductAttribute attribute = convertToEntity(attributeDTO);
        ProductAttribute savedAttribute = productAttributeRepository.save(attribute);
        log.info("상품 속성 생성: {}", savedAttribute.getName());
        return convertToDTO(savedAttribute);
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

        existingAttribute.setName(attributeDTO.getName());
        existingAttribute.setCode(attributeDTO.getCode());
        existingAttribute.setDescription(attributeDTO.getDescription());
        existingAttribute.setType(attributeDTO.getType());
        existingAttribute.setRequired(attributeDTO.isRequired());
        existingAttribute.setFilterable(attributeDTO.isFilterable());
        existingAttribute.setSearchable(attributeDTO.isSearchable());
        existingAttribute.setComparable(attributeDTO.isComparable());
        existingAttribute.setShowInProductListing(attributeDTO.isShowInProductListing());

        // 옵션은 별도로 관리하므로 여기서는 업데이트하지 않음

        ProductAttribute updatedAttribute = productAttributeRepository.save(existingAttribute);
        log.info("상품 속성 업데이트: {}", updatedAttribute.getName());
        return convertToDTO(updatedAttribute);
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
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductAttributeDTO> getSearchableAttributes() {
        return productAttributeRepository.findBySearchableTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductAttributeDTO> getComparableAttributes() {
        return productAttributeRepository.findByComparableTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductAttributeDTO> getAttributesForProductListing() {
        return productAttributeRepository.findByShowInProductListingTrue().stream()
                .map(this::convertToDTO)
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

    // 헬퍼 메서드
    private ProductAttributeDTO convertToDTO(ProductAttribute attribute) {
        return ProductAttributeDTO.builder()
                .id(attribute.getId())
                .name(attribute.getName())
                .code(attribute.getCode())
                .description(attribute.getDescription())
                .type(attribute.getType())
                .required(attribute.isRequired())
                .filterable(attribute.isFilterable())
                .searchable(attribute.isSearchable())
                .comparable(attribute.isComparable())
                .showInProductListing(attribute.isShowInProductListing())
                .options(new ArrayList<>(attribute.getOptions()))
                .createdAt(attribute.getCreatedAt())
                .updatedAt(attribute.getUpdatedAt())
                .build();
    }

    private ProductAttribute convertToEntity(ProductAttributeDTO dto) {
        ProductAttribute attribute = new ProductAttribute();
        attribute.setName(dto.getName());
        attribute.setCode(dto.getCode());
        attribute.setDescription(dto.getDescription());
        attribute.setType(dto.getType());
        attribute.setRequired(dto.isRequired());
        attribute.setFilterable(dto.isFilterable());
        attribute.setSearchable(dto.isSearchable());
        attribute.setComparable(dto.isComparable());
        attribute.setShowInProductListing(dto.isShowInProductListing());

        if (dto.getOptions() != null) {
            attribute.setOptions(new ArrayList<>(dto.getOptions()));
        }

        return attribute;
    }
}