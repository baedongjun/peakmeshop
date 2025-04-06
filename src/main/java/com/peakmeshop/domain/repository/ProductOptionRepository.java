package com.peakmeshop.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.domain.entity.ProductOption;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

    List<ProductOption> findByProductId(Long productId);

    List<ProductOption> findByProductIdAndNameAndValue(Long productId, String name, String value);

    Page<ProductOption> findByProductId(Long productId, Pageable pageable);
}