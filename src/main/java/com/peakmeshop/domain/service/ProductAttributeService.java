package com.peakmeshop.domain.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.ProductAttributeDTO;

public interface ProductAttributeService {

    Page<ProductAttributeDTO> getAllAttributes(Pageable pageable);

    Optional<ProductAttributeDTO> getAttributeById(Long id);

    Optional<ProductAttributeDTO> getAttributeByCode(String code);

    ProductAttributeDTO createAttribute(ProductAttributeDTO attributeDTO);

    ProductAttributeDTO updateAttribute(Long id, ProductAttributeDTO attributeDTO);

    boolean deleteAttribute(Long id);

    List<ProductAttributeDTO> getFilterableAttributes();

    List<ProductAttributeDTO> getSearchableAttributes();

    List<ProductAttributeDTO> getComparableAttributes();

    List<ProductAttributeDTO> getAttributesForProductListing();

    void addAttributeOption(Long attributeId, String option);

    void removeAttributeOption(Long attributeId, String option);

    List<String> getAttributeOptions(Long attributeId);
}