package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.ProductAttributeOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductAttributeOptionRepository extends JpaRepository<ProductAttributeOption, Long> {
    List<ProductAttributeOption> findByEnabledTrue();
    List<ProductAttributeOption> findByType(String type);
} 