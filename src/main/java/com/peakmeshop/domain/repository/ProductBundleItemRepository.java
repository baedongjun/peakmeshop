package com.peakmeshop.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.domain.entity.ProductBundle;
import com.peakmeshop.domain.entity.ProductBundleItem;

public interface ProductBundleItemRepository extends JpaRepository<ProductBundleItem, Long> {

    List<ProductBundleItem> findByBundleId(Long bundleId);

    List<ProductBundleItem> findByBundle(ProductBundle bundle);

    void deleteByBundleId(Long bundleId);
}