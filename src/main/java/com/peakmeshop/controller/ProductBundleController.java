package com.peakmeshop.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RestController;

import com.peakmeshop.dto.ProductBundleDTO;
import com.peakmeshop.service.ProductBundleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/product-bundles")
@RequiredArgsConstructor
public class ProductBundleController {

    private final ProductBundleService productBundleService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductBundleDTO> getBundleById(@PathVariable Long id) {
        ProductBundleDTO bundle = productBundleService.getBundleById(id);
        return ResponseEntity.ok(bundle);
    }

    @GetMapping
    public ResponseEntity<List<ProductBundleDTO>> getAllBundles() {
        List<ProductBundleDTO> bundles = productBundleService.getAllBundles();
        return ResponseEntity.ok(bundles);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<ProductBundleDTO>> getAllBundlesPaged(Pageable pageable) {
        Page<ProductBundleDTO> bundles = productBundleService.getAllBundles(pageable);
        return ResponseEntity.ok(bundles);
    }

    @GetMapping("/active")
    public ResponseEntity<Page<ProductBundleDTO>> getActiveBundles(Pageable pageable) {
        Page<ProductBundleDTO> bundles = productBundleService.getActiveBundles(pageable);
        return ResponseEntity.ok(bundles);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductBundleDTO> createBundle(@Valid @RequestBody ProductBundleDTO bundleDTO) {
        ProductBundleDTO createdBundle = productBundleService.createBundle(bundleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBundle);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductBundleDTO> updateBundle(
            @PathVariable Long id, @Valid @RequestBody ProductBundleDTO bundleDTO) {

        ProductBundleDTO updatedBundle = productBundleService.updateBundle(id, bundleDTO);
        return ResponseEntity.ok(updatedBundle);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBundle(@PathVariable Long id) {
        productBundleService.deleteBundle(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateBundle(@PathVariable Long id) {
        productBundleService.activateBundle(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateBundle(@PathVariable Long id) {
        productBundleService.deactivateBundle(id);
        return ResponseEntity.ok().build();
    }
}