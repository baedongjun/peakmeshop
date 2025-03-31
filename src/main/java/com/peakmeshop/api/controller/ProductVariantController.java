package com.peakmeshop.api.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.peakmeshop.api.dto.ProductVariantDTO;
import com.peakmeshop.domain.service.ProductVariantService;
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

@RestController
@RequestMapping("/api/product-variants")
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    public ProductVariantController(ProductVariantService productVariantService) {
        this.productVariantService = productVariantService;
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ProductVariantDTO>> getVariantsByProductId(
            @PathVariable Long productId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productVariantService.getVariantsByProductId(productId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductVariantDTO> getVariantById(@PathVariable Long id) {
        return productVariantService.getVariantById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductVariantDTO> getVariantBySku(@PathVariable String sku) {
        return productVariantService.getVariantBySku(sku)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductVariantDTO> createVariant(@RequestBody ProductVariantDTO variantDTO) {
        return new ResponseEntity<>(productVariantService.createVariant(variantDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductVariantDTO> updateVariant(
            @PathVariable Long id,
            @RequestBody ProductVariantDTO variantDTO) {
        return ResponseEntity.ok(productVariantService.updateVariant(id, variantDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVariant(@PathVariable Long id) {
        if (productVariantService.deleteVariant(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/product/{productId}/generate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductVariantDTO>> createVariantsFromCombinations(
            @PathVariable Long productId,
            @RequestBody Map<String, List<String>> attributeCombinations) {
        return ResponseEntity.ok(productVariantService.createVariantsFromCombinations(
                productId, attributeCombinations));
    }

    @PutMapping("/{id}/quantity")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateVariantQuantity(
            @PathVariable Long id,
            @RequestParam int quantity) {
        productVariantService.updateVariantQuantity(id, quantity);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/price")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateVariantPrice(
            @PathVariable Long id,
            @RequestParam BigDecimal price) {
        productVariantService.updateVariantPrice(id, price);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> enableVariant(@PathVariable Long id) {
        productVariantService.enableVariant(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> disableVariant(@PathVariable Long id) {
        productVariantService.disableVariant(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/product/{productId}/combinations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, String>>> getPossibleCombinations(@PathVariable Long productId) {
        return ResponseEntity.ok(productVariantService.getPossibleCombinations(productId));
    }

    @GetMapping("/{id}/in-stock")
    public ResponseEntity<Boolean> isVariantInStock(@PathVariable Long id) {
        return ResponseEntity.ok(productVariantService.isVariantInStock(id));
    }

    @GetMapping("/{id}/quantity")
    public ResponseEntity<Integer> getVariantQuantity(@PathVariable Long id) {
        return ResponseEntity.ok(productVariantService.getVariantQuantity(id));
    }
}