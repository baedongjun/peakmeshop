package com.peakmeshop.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.dto.ProductDTO;
import com.peakmeshop.dto.SearchDTO;

public interface SearchService {

    Page<ProductDTO> search(SearchDTO searchDTO, Pageable pageable);

    Page<ProductDTO> searchByKeyword(String keyword, Pageable pageable);

    Page<ProductDTO> searchByCategory(Long categoryId, Pageable pageable);

    Page<ProductDTO> searchByBrand(Long brandId, Pageable pageable);

    Page<ProductDTO> searchByPriceRange(Double minPrice, Double maxPrice, Pageable pageable);

    Page<ProductDTO> searchByAttribute(String attributeName, String attributeValue, Pageable pageable);

    Page<ProductDTO> searchInStock(boolean inStock, Pageable pageable);

    Page<ProductDTO> searchOnSale(boolean onSale, Pageable pageable);

    void indexAllProducts();

    void indexProduct(Long productId);

    void removeProductFromIndex(Long productId);
}