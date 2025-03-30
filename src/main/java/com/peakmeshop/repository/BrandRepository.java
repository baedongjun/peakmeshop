package com.peakmeshop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peakmeshop.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("SELECT b FROM Brand b WHERE b.isActive = true")
    List<Brand> findActiveBrands();

    @Query("SELECT b FROM Brand b WHERE b.isFeatured = true AND b.isActive = true")
    List<Brand> findFeaturedBrands();

    @Query("SELECT b FROM Brand b WHERE b.name LIKE %:keyword% OR b.description LIKE %:keyword%")
    List<Brand> searchBrands(@Param("keyword") String keyword);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.brand.id = :brandId")
    long countProductsByBrand(@Param("brandId") Long brandId);
}