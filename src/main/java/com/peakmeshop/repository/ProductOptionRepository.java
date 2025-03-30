package com.peakmeshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.peakmeshop.entity.ProductOption;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

    List<ProductOption> findByProductId(Long productId);

    List<ProductOption> findByProductIdAndNameAndValue(Long productId, String name, String value);
}