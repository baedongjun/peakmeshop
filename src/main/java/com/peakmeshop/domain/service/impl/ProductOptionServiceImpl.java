package com.peakmeshop.domain.service.impl;

import com.peakmeshop.api.dto.ProductOptionDTO;
import com.peakmeshop.api.dto.ProductOptionValueDTO;
import com.peakmeshop.api.mapper.ProductOptionMapper;
import com.peakmeshop.api.mapper.ProductOptionValueMapper;
import com.peakmeshop.domain.entity.Product;
import com.peakmeshop.domain.entity.ProductOption;
import com.peakmeshop.domain.entity.ProductOptionValue;
import com.peakmeshop.domain.repository.ProductOptionRepository;
import com.peakmeshop.domain.repository.ProductOptionValueRepository;
import com.peakmeshop.domain.repository.ProductRepository;
import com.peakmeshop.domain.service.ProductOptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductOptionServiceImpl implements ProductOptionService {

    private final ProductOptionRepository productOptionRepository;
    private final ProductOptionValueRepository productOptionValueRepository;
    private final ProductRepository productRepository;
    private final ProductOptionMapper productOptionMapper;
    private final ProductOptionValueMapper productOptionValueMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductOptionDTO> getOptionsByProductId(Long productId, Pageable pageable) {
        return productOptionRepository.findByProductId(productId, pageable)
                .map(productOptionMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductOptionDTO> getOptionById(Long id) {
        return productOptionRepository.findById(id)
                .map(productOptionMapper::toDTO);
    }

    @Override
    public ProductOptionDTO createOption(ProductOptionDTO optionDTO) {
        Product product = productRepository.findById(optionDTO.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + optionDTO.getProductId()));

        ProductOption option = productOptionMapper.toEntity(optionDTO);
        option.setProduct(product);

        ProductOption savedOption = productOptionRepository.save(option);
        log.info("상품 옵션 생성: 상품={}, 옵션={}", product.getName(), savedOption.getName());
        return productOptionMapper.toDTO(savedOption);
    }

    @Override
    public ProductOptionDTO updateOption(Long id, ProductOptionDTO optionDTO) {
        ProductOption existingOption = productOptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품 옵션을 찾을 수 없습니다: " + id));

        existingOption.setName(optionDTO.getName());
        existingOption.setDescription(optionDTO.getDescription());
        existingOption.setDisplayName(optionDTO.getDisplayName());
        existingOption.setSortOrder(optionDTO.getSortOrder());
        existingOption.setEnabled(optionDTO.isEnabled());

        ProductOption updatedOption = productOptionRepository.save(existingOption);
        log.info("상품 옵션 업데이트: 옵션={}", updatedOption.getName());
        return productOptionMapper.toDTO(updatedOption);
    }

    @Override
    public boolean deleteOption(Long id) {
        return productOptionRepository.findById(id).map(option -> {
            productOptionRepository.delete(option);
            log.info("상품 옵션 삭제: 옵션={}", option.getName());
            return true;
        }).orElse(false);
    }

    @Override
    public List<ProductOptionValueDTO> createOptionValues(Long optionId, List<ProductOptionValueDTO> valueDTOs) {
        ProductOption option = productOptionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("상품 옵션을 찾을 수 없습니다: " + optionId));

        List<ProductOptionValue> values = valueDTOs.stream()
                .map(productOptionValueMapper::toEntity)
                .peek(value -> value.setOption(option))
                .collect(Collectors.toList());

        List<ProductOptionValue> savedValues = productOptionValueRepository.saveAll(values);
        log.info("상품 옵션 값 생성: 옵션={}, 개수={}", option.getName(), savedValues.size());
        return savedValues.stream()
                .map(productOptionValueMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void updateOptionValue(Long id, ProductOptionValueDTO valueDTO) {
        ProductOptionValue existingValue = productOptionValueRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품 옵션 값을 찾을 수 없습니다: " + id));

        existingValue.setName(valueDTO.getName());
        existingValue.setValue(valueDTO.getValue());
        existingValue.setSortOrder(valueDTO.getSortOrder());
        existingValue.setAdditionalPrice(valueDTO.getAdditionalPrice());
        existingValue.setEnabled(valueDTO.isEnabled());
        existingValue.setStock(valueDTO.getStock());

        productOptionValueRepository.save(existingValue);
        log.info("상품 옵션 값 업데이트: 값={}", existingValue.getName());
    }

    @Override
    public void deleteOptionValue(Long id) {
        productOptionValueRepository.findById(id).ifPresent(value -> {
            productOptionValueRepository.delete(value);
            log.info("상품 옵션 값 삭제: 값={}", value.getName());
        });
    }

    @Override
    public void enableOption(Long id) {
        productOptionRepository.findById(id).ifPresent(option -> {
            option.setEnabled(true);
            productOptionRepository.save(option);
            log.info("상품 옵션 활성화: 옵션={}", option.getName());
        });
    }

    @Override
    public void disableOption(Long id) {
        productOptionRepository.findById(id).ifPresent(option -> {
            option.setEnabled(false);
            productOptionRepository.save(option);
            log.info("상품 옵션 비활성화: 옵션={}", option.getName());
        });
    }

    @Override
    public void updateOptionSortOrder(Long id, Integer sortOrder) {
        productOptionRepository.findById(id).ifPresent(option -> {
            option.setSortOrder(sortOrder);
            productOptionRepository.save(option);
            log.info("상품 옵션 정렬 순서 업데이트: 옵션={}, 순서={}", option.getName(), sortOrder);
        });
    }

    @Override
    public void updateOptionValueAdditionalPrice(Long id, BigDecimal additionalPrice) {
        productOptionValueRepository.findById(id).ifPresent(optionValue -> {
            optionValue.setAdditionalPrice(additionalPrice);
            productOptionValueRepository.save(optionValue);
            log.info("상품 옵션 추가 가격 업데이트: 옵션={}, 가격={}", optionValue.getName(), additionalPrice);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isOptionValueInStock(Long id) {
        return productOptionValueRepository.findById(id)
                .map(value -> value.getStock() > 0)
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public int getOptionValueStock(Long id) {
        return productOptionValueRepository.findById(id)
                .map(ProductOptionValue::getStock)
                .orElse(0);
    }

    @Override
    public void updateOptionValueStock(Long id, int stock) {
        productOptionValueRepository.findById(id).ifPresent(value -> {
            value.setStock(stock);
            productOptionValueRepository.save(value);
            log.info("상품 옵션 값 재고 업데이트: 값={}, 재고={}", value.getName(), stock);
        });
    }
} 