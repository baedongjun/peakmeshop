package com.peakmeshop.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peakmeshop.dto.ProductAttributeDTO;
import com.peakmeshop.service.ProductAttributeService;

@RestController
@RequestMapping("/api/product-attributes")
public class ProductAttributeController {

    private final ProductAttributeService productAttributeService;

    public ProductAttributeController(ProductAttributeService productAttributeService) {
        this.productAttributeService = productAttributeService;
    }

    @GetMapping
    public ResponseEntity<Page<ProductAttributeDTO>> getAllAttributes(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productAttributeService.getAllAttributes(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductAttributeDTO> getAttributeById(@PathVariable Long id) {
        return productAttributeService.getAttributeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ProductAttributeDTO> getAttributeByCode(@PathVariable String code) {
        return productAttributeService.getAttributeByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductAttributeDTO> createAttribute(@RequestBody ProductAttributeDTO attributeDTO) {
        return new ResponseEntity<>(productAttributeService.createAttribute(attributeDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductAttributeDTO> updateAttribute(
            @PathVariable Long id,
            @RequestBody ProductAttributeDTO attributeDTO) {
        return ResponseEntity.ok(productAttributeService.updateAttribute(id, attributeDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAttribute(@PathVariable Long id) {
        if (productAttributeService.deleteAttribute(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/filterable")
    public ResponseEntity<List<ProductAttributeDTO>> getFilterableAttributes() {
        return ResponseEntity.ok(productAttributeService.getFilterableAttributes());
    }

    @GetMapping("/searchable")
    public ResponseEntity<List<ProductAttributeDTO>> getSearchableAttributes() {
        return ResponseEntity.ok(productAttributeService.getSearchableAttributes());
    }

    @GetMapping("/comparable")
    public ResponseEntity<List<ProductAttributeDTO>> getComparableAttributes() {
        return ResponseEntity.ok(productAttributeService.getComparableAttributes());
    }

    @GetMapping("/product-listing")
    public ResponseEntity<List<ProductAttributeDTO>> getAttributesForProductListing() {
        return ResponseEntity.ok(productAttributeService.getAttributesForProductListing());
    }

    @PostMapping("/{attributeId}/options")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addAttributeOption(
            @PathVariable Long attributeId,
            @RequestParam String option) {
        productAttributeService.addAttributeOption(attributeId, option);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{attributeId}/options")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeAttributeOption(
            @PathVariable Long attributeId,
            @RequestParam String option) {
        productAttributeService.removeAttributeOption(attributeId, option);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{attributeId}/options")
    public ResponseEntity<List<String>> getAttributeOptions(@PathVariable Long attributeId) {
        return ResponseEntity.ok(productAttributeService.getAttributeOptions(attributeId));
    }
}