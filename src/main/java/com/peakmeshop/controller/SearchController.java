package com.peakmeshop.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peakmeshop.dto.ProductDTO;
import com.peakmeshop.dto.SearchDTO;
import com.peakmeshop.service.SearchService;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping
    public ResponseEntity<Page<ProductDTO>> search(
            @RequestBody SearchDTO searchDTO,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(searchService.search(searchDTO, pageable));
    }

    @GetMapping("/keyword")
    public ResponseEntity<Page<ProductDTO>> searchByKeyword(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(searchService.searchByKeyword(keyword, pageable));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductDTO>> searchByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(searchService.searchByCategory(categoryId, pageable));
    }

    @GetMapping("/brand/{brandId}")
    public ResponseEntity<Page<ProductDTO>> searchByBrand(
            @PathVariable Long brandId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(searchService.searchByBrand(brandId, pageable));
    }

    @GetMapping("/price")
    public ResponseEntity<Page<ProductDTO>> searchByPriceRange(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(searchService.searchByPriceRange(minPrice, maxPrice, pageable));
    }

    @GetMapping("/attribute")
    public ResponseEntity<Page<ProductDTO>> searchByAttribute(
            @RequestParam String name,
            @RequestParam String value,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(searchService.searchByAttribute(name, value, pageable));
    }

    @GetMapping("/in-stock")
    public ResponseEntity<Page<ProductDTO>> searchInStock(
            @RequestParam(defaultValue = "true") boolean inStock,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(searchService.searchInStock(inStock, pageable));
    }

    @GetMapping("/on-sale")
    public ResponseEntity<Page<ProductDTO>> searchOnSale(
            @RequestParam(defaultValue = "true") boolean onSale,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(searchService.searchOnSale(onSale, pageable));
    }

    @PostMapping("/index/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> indexAllProducts() {
        searchService.indexAllProducts();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/index/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> indexProduct(@PathVariable Long productId) {
        searchService.indexProduct(productId);
        return ResponseEntity.ok().build();
    }
}