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

import com.peakmeshop.dto.ProductDTO;
import com.peakmeshop.dto.SupplierDTO;
import com.peakmeshop.service.SupplierService;

@RestController
@RequestMapping("/api/suppliers")
@PreAuthorize("hasRole('ADMIN')")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public ResponseEntity<Page<SupplierDTO>> getAllSuppliers(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(supplierService.getAllSuppliers(pageable));
    }

    @GetMapping("/active")
    public ResponseEntity<Page<SupplierDTO>> getActiveSuppliers(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(supplierService.getActiveSuppliers(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable Long id) {
        return supplierService.getSupplierById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<SupplierDTO> getSupplierByCode(@PathVariable String code) {
        return supplierService.getSupplierByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SupplierDTO> createSupplier(@RequestBody SupplierDTO supplierDTO) {
        return new ResponseEntity<>(supplierService.createSupplier(supplierDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierDTO> updateSupplier(
            @PathVariable Long id,
            @RequestBody SupplierDTO supplierDTO) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, supplierDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        if (supplierService.deleteSupplier(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{supplierId}/products")
    public ResponseEntity<Page<ProductDTO>> getSupplierProducts(
            @PathVariable Long supplierId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(supplierService.getSupplierProducts(supplierId, pageable));
    }

    @PostMapping("/{supplierId}/products/{productId}")
    public ResponseEntity<Void> addProductToSupplier(
            @PathVariable Long supplierId,
            @PathVariable Long productId) {
        supplierService.addProductToSupplier(supplierId, productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{supplierId}/products/{productId}")
    public ResponseEntity<Void> removeProductFromSupplier(
            @PathVariable Long supplierId,
            @PathVariable Long productId) {
        supplierService.removeProductFromSupplier(supplierId, productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<SupplierDTO>> searchSuppliers(@RequestParam String keyword) {
        return ResponseEntity.ok(supplierService.searchSuppliers(keyword));
    }
}