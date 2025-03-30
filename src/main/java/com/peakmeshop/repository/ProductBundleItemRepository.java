package com.peakmeshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.entity.ProductBundle;
import com.peakmeshop.entity.ProductBundleItem;

public interface ProductBundleItemRepository extends JpaRepository<ProductBundleItem, Long> {

    List<ProductBundleItem> findByBundleId(Long bundleId);

    List<ProductBundleItem> findByBundle(ProductBundle bundle);

    void deleteByBundleId(Long bundleId);
}