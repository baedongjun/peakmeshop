package com.peakmeshop.repository;

import java.util.List;
import java.util.Optional;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.peakmeshop.entity.ProductAttribute;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {

    Optional<ProductAttribute> findByCode(String code);

    List<ProductAttribute> findByFilterableTrue();

    List<ProductAttribute> findBySearchableTrue();

    List<ProductAttribute> findByComparableTrue();

    List<ProductAttribute> findByShowInProductListingTrue();

    List<ProductAttribute> findByProduct_Id(Long productId);

    @Query("SELECT pa FROM ProductAttribute pa WHERE pa.product.id = :productId")
    List<ProductAttribute> findByProductId(@Param("productId") Long productId);
}