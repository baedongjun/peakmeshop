package com.peakmeshop.domain.repository;

import java.util.List;

import com.peakmeshop.domain.entity.ProductOptionValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.domain.entity.ProductOption;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

    List<ProductOption> findByProductId(Long productId);

    List<ProductOption> findByProductIdAndNameAndValues(Long productId, String name, List<ProductOptionValue> values);

    Page<ProductOption> findByProductId(Long productId, Pageable pageable);
}