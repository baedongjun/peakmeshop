package com.peakmeshop.api.controller;

import java.util.List;

import com.peakmeshop.api.dto.MemberDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peakmeshop.api.dto.ProductDTO;
import com.peakmeshop.domain.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(@PageableDefault(size = 10) Pageable pageable) {
        Page<ProductDTO> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        ProductDTO createdProduct = productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductDTO>> getProductsByCategory(
            @PathVariable Long categoryId,
            Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId, pageable));
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<Page<ProductDTO>> getProductsByBrand(
            @PathVariable String brand,
            Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsByBrand(brand, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductDTO>> searchProducts(
            @RequestParam String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(productService.searchProducts(keyword, pageable));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductDTO> updateProductStock(
            @PathVariable Long id,
            @RequestParam int stock) {
        return ResponseEntity.ok(productService.updateProductStock(id, stock));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductDTO> updateProductStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(productService.updateProductStatus(id, status));
    }

    @GetMapping("/featured")
    public ResponseEntity<Page<ProductDTO>> getFeaturedProducts(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.getFeaturedProducts(pageable));
    }

    @GetMapping("/new-arrivals")
    public ResponseEntity<Page<ProductDTO>> getNewArrivals(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.getNewArrivals(pageable));
    }

    @GetMapping("/best-sellers")
    public ResponseEntity<Page<ProductDTO>> getBestSellers(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.getBestSellers(pageable));
    }

    @GetMapping("/discounted")
    public ResponseEntity<Page<ProductDTO>> getDiscountedProducts(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.getDiscountedProducts(pageable));
    }
}