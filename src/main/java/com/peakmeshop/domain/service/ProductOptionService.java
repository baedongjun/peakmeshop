package com.peakmeshop.domain.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.ProductOptionDTO;
import com.peakmeshop.api.dto.ProductOptionValueDTO;

public interface ProductOptionService {

    Page<ProductOptionDTO> getOptionsByProductId(Long productId, Pageable pageable);

    Optional<ProductOptionDTO> getOptionById(Long id);

    ProductOptionDTO createOption(ProductOptionDTO optionDTO);

    ProductOptionDTO updateOption(Long id, ProductOptionDTO optionDTO);

    boolean deleteOption(Long id);

    List<ProductOptionValueDTO> createOptionValues(Long optionId, List<ProductOptionValueDTO> values);

    void updateOptionValue(Long id, ProductOptionValueDTO valueDTO);

    void deleteOptionValue(Long id);

    void enableOption(Long id);

    void disableOption(Long id);

    void updateOptionSortOrder(Long id, Integer sortOrder);

    void updateOptionValueAdditionalPrice(Long id, BigDecimal additionalPrice);

    boolean isOptionValueInStock(Long id);

    int getOptionValueStock(Long id);

    void updateOptionValueStock(Long id, int stock);
} 