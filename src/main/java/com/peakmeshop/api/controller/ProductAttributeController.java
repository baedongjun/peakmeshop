package com.peakmeshop.api.controller;

import com.peakmeshop.api.dto.ProductAttributeDTO;
import com.peakmeshop.domain.entity.ProductAttributeOption;
import com.peakmeshop.domain.service.ProductAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-attributes")
@RequiredArgsConstructor
public class ProductAttributeController {

    private final ProductAttributeService productAttributeService;

    @GetMapping
    public ResponseEntity<Page<ProductAttributeDTO>> getAllAttributes(Pageable pageable) {
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
    public ResponseEntity<ProductAttributeDTO> createAttribute(@RequestBody ProductAttributeDTO attributeDTO) {
        return ResponseEntity.ok(productAttributeService.createAttribute(attributeDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductAttributeDTO> updateAttribute(
            @PathVariable Long id,
            @RequestBody ProductAttributeDTO attributeDTO) {
        return ResponseEntity.ok(productAttributeService.updateAttribute(id, attributeDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttribute(@PathVariable Long id) {
        if (productAttributeService.deleteAttribute(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/product/{productId}/options")
    public ResponseEntity<List<String>> getAttributeOptions(
            @PathVariable Long productId,
            @RequestParam String type) {
        return ResponseEntity.ok(productAttributeService.getAttributeOptions(productId, type));
    }

    @GetMapping("/option/{attributeOptionId}/values")
    public ResponseEntity<List<String>> getAttributeOptionValues(@PathVariable Long attributeOptionId) {
        return ResponseEntity.ok(productAttributeService.getAttributeOptionValues(attributeOptionId));
    }

    @GetMapping("/option/{attributeOptionId}")
    public ResponseEntity<ProductAttributeOption> getAttributeOption(@PathVariable Long attributeOptionId) {
        return ResponseEntity.ok(productAttributeService.getAttributeOption(attributeOptionId));
    }

    @GetMapping("/option/{attributeOptionId}/code")
    public ResponseEntity<String> getAttributeOptionCode(@PathVariable Long attributeOptionId) {
        return ResponseEntity.ok(productAttributeService.getCode(attributeOptionId));
    }
}