package com.peakmeshop.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.dto.ProductBundleDTO;

public interface ProductBundleService {

    ProductBundleDTO getBundleById(Long id);

    List<ProductBundleDTO> getAllBundles();

    Page<ProductBundleDTO> getAllBundles(Pageable pageable);

    Page<ProductBundleDTO> getActiveBundles(Pageable pageable);

    ProductBundleDTO createBundle(ProductBundleDTO bundleDTO);

    ProductBundleDTO updateBundle(Long id, ProductBundleDTO bundleDTO);

    void deleteBundle(Long id);

    void activateBundle(Long id);

    void deactivateBundle(Long id);
}