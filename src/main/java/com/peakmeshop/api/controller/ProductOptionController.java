package com.peakmeshop.api.controller;

import java.math.BigDecimal;
import java.util.List;

import com.peakmeshop.api.dto.ProductOptionDTO;
import com.peakmeshop.api.dto.ProductOptionValueDTO;
import com.peakmeshop.domain.service.ProductOptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product-options")
public class ProductOptionController {

    private final ProductOptionService productOptionService;

    public ProductOptionController(ProductOptionService productOptionService) {
        this.productOptionService = productOptionService;
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ProductOptionDTO>> getOptionsByProductId(
            @PathVariable Long productId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productOptionService.getOptionsByProductId(productId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductOptionDTO> getOptionById(@PathVariable Long id) {
        return productOptionService.getOptionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductOptionDTO> createOption(@RequestBody ProductOptionDTO optionDTO) {
        return new ResponseEntity<>(productOptionService.createOption(optionDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductOptionDTO> updateOption(
            @PathVariable Long id,
            @RequestBody ProductOptionDTO optionDTO) {
        return ResponseEntity.ok(productOptionService.updateOption(id, optionDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOption(@PathVariable Long id) {
        if (productOptionService.deleteOption(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{optionId}/values")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductOptionValueDTO>> createOptionValues(
            @PathVariable Long optionId,
            @RequestBody List<ProductOptionValueDTO> values) {
        return ResponseEntity.ok(productOptionService.createOptionValues(optionId, values));
    }

    @PutMapping("/values/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateOptionValue(
            @PathVariable Long id,
            @RequestBody ProductOptionValueDTO valueDTO) {
        productOptionService.updateOptionValue(id, valueDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/values/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOptionValue(@PathVariable Long id) {
        productOptionService.deleteOptionValue(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> enableOption(@PathVariable Long id) {
        productOptionService.enableOption(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> disableOption(@PathVariable Long id) {
        productOptionService.disableOption(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/sort-order")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateOptionSortOrder(
            @PathVariable Long id,
            @RequestParam Integer sortOrder) {
        productOptionService.updateOptionSortOrder(id, sortOrder);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/additional-price")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateOptionAdditionalPrice(
            @PathVariable Long id,
            @RequestParam BigDecimal additionalPrice) {
        productOptionService.updateOptionValueAdditionalPrice(id, additionalPrice);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/values/{id}/in-stock")
    public ResponseEntity<Boolean> isOptionValueInStock(@PathVariable Long id) {
        return ResponseEntity.ok(productOptionService.isOptionValueInStock(id));
    }

    @GetMapping("/values/{id}/stock")
    public ResponseEntity<Integer> getOptionValueStock(@PathVariable Long id) {
        return ResponseEntity.ok(productOptionService.getOptionValueStock(id));
    }

    @PutMapping("/values/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateOptionValueStock(
            @PathVariable Long id,
            @RequestParam int stock) {
        productOptionService.updateOptionValueStock(id, stock);
        return ResponseEntity.ok().build();
    }
} 