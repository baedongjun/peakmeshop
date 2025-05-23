package com.peakmeshop.domain.service;

import java.util.List;
import java.util.Optional;

import com.peakmeshop.domain.entity.ProductOption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.ProductAttributeDTO;
import com.peakmeshop.domain.entity.ProductAttributeOption;

public interface ProductAttributeService {

    Page<ProductAttributeDTO> getAllAttributes(Pageable pageable);

    Optional<ProductAttributeDTO> getAttributeById(Long id);

    Optional<ProductAttributeDTO> getAttributeByCode(String code);

    ProductAttributeDTO createAttribute(ProductAttributeDTO attributeDTO);

    ProductAttributeDTO updateAttribute(Long id, ProductAttributeDTO attributeDTO);

    boolean deleteAttribute(Long id);

    void addAttributeOption(Long attributeId, ProductOption option);

    void removeAttributeOption(Long attributeId, ProductOption option);

    List<String> getAttributeOptions(Long productId, String type);

    List<String> getAttributeOptionValues(Long attributeOptionId);

    ProductAttributeOption getAttributeOption(Long attributeOptionId);

    String getCode(Long attributeOptionId);
}